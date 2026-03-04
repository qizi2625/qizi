package com.example.houserentalsystem.controller.test;

import com.example.houserentalsystem.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/redis")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;  // 只用 RedisUtil，不需要 RedisTemplate

    // ==================== 基础功能 ====================

    @GetMapping("/set")
    public Map<String, Object> setValue(@RequestParam String key, @RequestParam String value) {
        redisUtil.set(key, value);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("message", "设置成功");
        return result;
    }

    @GetMapping("/get")
    public Map<String, Object> getValue(@RequestParam String key) {
        Object value = redisUtil.get(key, Object.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("exists", value != null);
        return result;
    }

    @GetMapping("/setWithExpire")
    public Map<String, Object> setWithExpire(
            @RequestParam String key, 
            @RequestParam String value,
            @RequestParam long timeout) {
        redisUtil.set(key, value, timeout);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("timeout", timeout);
        result.put("message", "设置成功（带过期时间）");
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        boolean success = redisUtil.delete(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败（key不存在）");
        return result;
    }

    // ==================== 对象存储 ====================

    @GetMapping("/setObject")
    public Map<String, Object> setObject() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "测试用户");
        user.put("email", "test@example.com");
        user.put("createTime", LocalDateTime.now().toString());  // 转为String避免序列化问题
        
        redisUtil.set("test:user", user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", "test:user");
        result.put("value", user);
        result.put("message", "对象存储成功");
        return result;
    }

    @GetMapping("/getObject")
    public Map<String, Object> getObject() {
        Object user = redisUtil.get("test:user", Object.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", "test:user");
        result.put("value", user);
        result.put("exists", user != null);
        return result;
    }

    @GetMapping("/setList")
    public Map<String, Object> setList() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            item.put("name", "商品" + i);
            item.put("price", 100 * i);
            list.add(item);
        }
        
        redisUtil.set("test:list", list);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", "test:list");
        result.put("value", list);
        result.put("message", "列表存储成功");
        return result;
    }

    @GetMapping("/getList")
    public Map<String, Object> getList() {
        Object list = redisUtil.get("test:list", Object.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", "test:list");
        result.put("value", list);
        result.put("exists", list != null);
        return result;
    }

    // ==================== 计数器 ====================

    @GetMapping("/increment")
    public Map<String, Object> increment(
            @RequestParam String key,
            @RequestParam(defaultValue = "1") long delta) {
        Long value = redisUtil.increment(key, delta);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("delta", delta);
        result.put("currentValue", value);
        return result;
    }

    // ==================== 其他操作 ====================

    @GetMapping("/hasKey")
    public Map<String, Object> hasKey(@RequestParam String key) {
        boolean exists = redisUtil.hasKey(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("exists", exists);
        return result;
    }

    @GetMapping("/expire")
    public Map<String, Object> expire(@RequestParam String key, @RequestParam long timeout) {
        boolean success = redisUtil.expire(key, timeout);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("timeout", timeout);
        result.put("success", success);
        result.put("message", success ? "过期时间设置成功" : "设置失败");
        return result;
    }

    @GetMapping("/getExpire")
    public Map<String, Object> getExpire(@RequestParam String key) {
        Long expire = redisUtil.getExpire(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("expire", expire);
        result.put("message", expire != null && expire > 0 ? 
                   "还有 " + expire + " 秒过期" : "永不过期或不存在");
        return result;
    }

    // ==================== 字符串操作（直接获取字符串）====================

    @GetMapping("/getString")
    public Map<String, Object> getString(@RequestParam String key) {
        String value = redisUtil.getString(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("exists", value != null);
        return result;
    }

    // ==================== 清理 ====================

    @DeleteMapping("/clean")
    public Map<String, Object> clean() {
        List<String> keys = List.of("test:hello", "test:expire", "test:counter", 
                                    "test:user", "test:list");
        int count = 0;
        for (String key : keys) {
            if (redisUtil.delete(key)) {
                count++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", count);
        result.put("message", "清理完成");
        return result;
    }
}