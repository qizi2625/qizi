package com.example.houserentalsystem.service.impl;

import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.entity.*;
import com.example.houserentalsystem.mapper.*;
import com.example.houserentalsystem.service.RecommendService;
import com.example.houserentalsystem.utils.RecommendationAlgorithm;
import com.example.houserentalsystem.vo.RecommendHouseVO;
import com.example.houserentalsystem.vo.TagVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.example.houserentalsystem.dto.HouseQueryDTO; 
import com.example.houserentalsystem.entity.HouseImage; 
import com.example.houserentalsystem.mapper.HouseImageMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private RecommendationAlgorithm algorithm;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String RECOMMEND_CACHE_KEY = "recommend:user:";
    private static final String HOT_HOUSES_KEY = "recommend:hot";
    private static final String NEW_HOUSES_KEY = "recommend:new";
    private static final int CACHE_EXPIRE_HOURS = 24;

    @Override
    public List<RecommendHouseVO> getPersonalizedRecommendations(Long userId, int size) {
        // 1. 尝试从缓存获取
        String cacheKey = RECOMMEND_CACHE_KEY + userId;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<RecommendHouseVO>>() {});
            } catch (Exception e) {
                // 缓存解析失败，重新计算
            }
        }

        // 2. 获取用户画像
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            // 新用户：返回热门房源
            return getHotHouses(size);
        }

        // 3. 解析用户偏好标签
        Map<String, Double> tagPreferences = new HashMap<>();
        if (profile.getPreferredTags() != null) {
            try {
                tagPreferences = objectMapper.readValue(
                        profile.getPreferredTags(),
                        new TypeReference<Map<String, Double>>() {}
                );
            } catch (Exception e) {
                // 解析失败，使用空Map
            }
        }

        // 4. 获取所有可推荐房源（排除已浏览/收藏的）
        List<House> allHouses = getAllRecommendableHouses();
        
        // 5. 获取用户已交互的房源ID
        List<Long> interactedHouseIds = getInteractedHouseIds(userId);

        // 6. 计算每个房源的匹配分数
        Map<House, Double> scores = new HashMap<>();
        for (House house : allHouses) {
            if (interactedHouseIds.contains(house.getId())) {
                continue; // 跳过已交互的房源
            }

            List<Tag> houseTags = tagMapper.findByHouseId(house.getId());
            
            double score = algorithm.calculateMatchScore(
                    tagPreferences,
                    houseTags,
                    profile.getPriceMin(),
                    profile.getPriceMax(),
                    house.getPrice()
            );

            // 加上热度加成（浏览量和收藏量）
            double popularityBonus = calculatePopularityScore(house);
            score = score * 0.7 + popularityBonus * 0.3;

            scores.put(house, score);
        }

        // 7. 排序并返回结果
        List<House> topHouses = algorithm.sortRecommendations(
                new ArrayList<>(scores.keySet()),
                scores,
                size
        );

        List<RecommendHouseVO> result = convertToRecommendVO(topHouses, scores);

        // 8. 缓存结果
        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(result),
                    CACHE_EXPIRE_HOURS,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            // 缓存失败不影响返回结果
        }

        return result;
    }

    @Override
    public List<RecommendHouseVO> getSimilarHouses(Long houseId, int size) {
        // 1. 获取目标房源
        House targetHouse = houseMapper.findById(houseId);
        if (targetHouse == null) {
            throw new BusinessException("房源不存在");
        }

        // 2. 获取目标房源的标签
        List<Tag> targetTags = tagMapper.findByHouseId(houseId);

        // 3. 获取所有其他房源
        List<House> allHouses = getAllRecommendableHouses();

        // 4. 计算相似度
        Map<House, Double> similarities = new HashMap<>();
        for (House house : allHouses) {
            if (house.getId().equals(houseId)) {
                continue; // 跳过自身
            }

            List<Tag> houseTags = tagMapper.findByHouseId(house.getId());
            double similarity = algorithm.calculateHouseSimilarity(targetTags, houseTags);
            
            // 结合价格相似度
            double priceSimilarity = calculatePriceSimilarity(targetHouse.getPrice(), house.getPrice());
            similarity = similarity * 0.8 + priceSimilarity * 0.2;

            similarities.put(house, similarity);
        }

        // 5. 排序返回
        List<House> similarHouses = algorithm.sortRecommendations(
                new ArrayList<>(similarities.keySet()),
                similarities,
                size
        );

        return convertToRecommendVO(similarHouses, similarities);
    }

    @Override
    public List<RecommendHouseVO> getHotHouses(int size) {
        // 1. 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(HOT_HOUSES_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<RecommendHouseVO>>() {});
            } catch (Exception e) {
                // 缓存解析失败，重新计算
            }
        }

        // 2. 计算热门房源（基于浏览量和收藏量）
        List<House> allHouses = getAllRecommendableHouses();
        
        // 计算热度分数
        Map<House, Double> hotScores = new HashMap<>();
        for (House house : allHouses) {
            double score = calculatePopularityScore(house);
            hotScores.put(house, score);
        }

        // 3. 排序返回
        List<House> hotHouses = algorithm.sortRecommendations(
                new ArrayList<>(hotScores.keySet()),
                hotScores,
                size
        );

        List<RecommendHouseVO> result = convertToRecommendVO(hotHouses, hotScores);

        // 4. 缓存结果
        try {
            redisTemplate.opsForValue().set(
                    HOT_HOUSES_KEY,
                    objectMapper.writeValueAsString(result),
                    6,  // 热门房源6小时更新一次
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            // 缓存失败不影响返回结果
        }

        return result;
    }

    @Override
    public List<RecommendHouseVO> getNewHouses(int size) {
        // 1. 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(NEW_HOUSES_KEY);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<RecommendHouseVO>>() {});
            } catch (Exception e) {
                // 缓存解析失败，重新计算
            }
        }

        // 2. 获取最新房源
        HouseQueryDTO queryDTO = new HouseQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(size);
        queryDTO.setSortBy("create_time");
        queryDTO.setSortOrder("desc");
        
        List<House> newHouses = houseMapper.pageQuery(queryDTO);

        List<RecommendHouseVO> result = convertToRecommendVO(newHouses, null);

        // 3. 缓存结果
        try {
            redisTemplate.opsForValue().set(
                    NEW_HOUSES_KEY,
                    objectMapper.writeValueAsString(result),
                    1,  // 最新房源1小时更新一次
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            // 缓存失败不影响返回结果
        }

        return result;
    }

    @Override
    public List<RecommendHouseVO> getDailyPicks(Long userId, int size) {
        // 混合推荐：60%个性化 + 30%热门 + 10%最新
        int personalSize = (int)(size * 0.6);
        int hotSize = (int)(size * 0.3);
        int newSize = size - personalSize - hotSize;

        List<RecommendHouseVO> result = new ArrayList<>();
        
        // 获取个性化推荐
        if (personalSize > 0) {
            result.addAll(getPersonalizedRecommendations(userId, personalSize));
        }
        
        // 获取热门推荐
        if (hotSize > 0) {
            result.addAll(getHotHouses(hotSize));
        }
        
        // 获取最新推荐
        if (newSize > 0) {
            result.addAll(getNewHouses(newSize));
        }

        // 去重
        return result.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> 
                                new TreeSet<>(Comparator.comparing(RecommendHouseVO::getId))),
                        ArrayList::new
                ));
    }

    @Override
    public void refreshUserRecommendations(Long userId) {
        String cacheKey = RECOMMEND_CACHE_KEY + userId;
        redisTemplate.delete(cacheKey);
        // 触发重新计算（下次访问时）
    }

    /**
     * 获取所有可推荐的房源（已上架的）
     */
    private List<House> getAllRecommendableHouses() {
        HouseQueryDTO queryDTO = new HouseQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(1000); // 获取足够多的房源
        return houseMapper.pageQuery(queryDTO);
    }

    /**
     * 获取用户已交互的房源ID
     */
    private List<Long> getInteractedHouseIds(Long userId) {
        List<UserBehavior> behaviors = userBehaviorMapper.findByUserId(userId);
        return behaviors.stream()
                .map(UserBehavior::getHouseId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 计算房源热度分数
     */
    private double calculatePopularityScore(House house) {
        // 热度公式：浏览数 * 0.3 + 收藏数 * 0.7
        int viewCount = house.getViewCount() != null ? house.getViewCount() : 0;
        int favoriteCount = house.getFavoriteCount() != null ? house.getFavoriteCount() : 0;
        
        return viewCount * 0.3 + favoriteCount * 0.7;
    }

    /**
     * 计算价格相似度
     */
    private double calculatePriceSimilarity(BigDecimal price1, BigDecimal price2) {
        if (price1 == null || price2 == null || price1.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        double ratio = Math.min(price1.doubleValue(), price2.doubleValue()) /
                      Math.max(price1.doubleValue(), price2.doubleValue());
        return ratio;
    }

    /**
     * 转换为推荐VO
     */
    private List<RecommendHouseVO> convertToRecommendVO(List<House> houses, Map<House, Double> scores) {
        return houses.stream()
                .map(house -> {
                    RecommendHouseVO vo = new RecommendHouseVO();
                    BeanUtils.copyProperties(house, vo);
                    
                    // 设置封面图
                    List<HouseImage> images = houseImageMapper.findByHouseId(house.getId());
                    images.stream()
                            .filter(img -> img.getType() == 1)
                            .findFirst()
                            .ifPresent(img -> vo.setCoverImage(img.getImageUrl()));
                    
                    // 设置标签
                    List<Tag> tags = tagMapper.findByHouseId(house.getId());
                    List<TagVO> tagVOs = tags.stream()
                            .map(tag -> {
                                TagVO tagVO = new TagVO();
                                BeanUtils.copyProperties(tag, tagVO);
                                return tagVO;
                            })
                            .collect(Collectors.toList());
                    vo.setTags(tagVOs);
                    
                    // 设置匹配分数
                    if (scores != null) {
                        vo.setMatchScore(scores.get(house));
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Autowired
    private HouseImageMapper houseImageMapper;
}