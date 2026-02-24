package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    
    // 根据用户ID查询画像
    UserProfile findByUserId(@Param("userId") Long userId);
    
    // 插入或更新画像
    int insertOrUpdate(UserProfile profile);
    
    // 更新偏好标签
    int updatePreferredTags(@Param("userId") Long userId, @Param("preferredTags") String preferredTags);
    
    // 更新价格偏好
    int updatePricePreference(@Param("userId") Long userId, 
                              @Param("priceMin") java.math.BigDecimal priceMin,
                              @Param("priceMax") java.math.BigDecimal priceMax);
    
    // 更新区域偏好
    int updateDistricts(@Param("userId") Long userId, @Param("preferredDistricts") String preferredDistricts);
}