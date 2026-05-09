package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类管理
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "商店相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;

    //查询店铺状态
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询到的营业状态是",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
