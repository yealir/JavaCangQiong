package com.sky.controller.admin;


import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";
    //注入操作redis对象
    @Autowired
    RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status){
        log.info("修改当前店铺状态为：{}",status == 1 ? "营业" : "打烊");
        redisTemplate.opsForValue().set(KEY , status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取当前店铺状态为：{}",status == 1 ? "营业" : "打烊");
        return Result.success(status);
    }
}
