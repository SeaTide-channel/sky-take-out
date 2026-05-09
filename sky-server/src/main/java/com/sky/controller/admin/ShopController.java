package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "商店相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;


    //设置营业状态
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置营业状态",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY,status);

        return Result.success();
    }

    //查询店铺状态
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询到的营业状态是",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
