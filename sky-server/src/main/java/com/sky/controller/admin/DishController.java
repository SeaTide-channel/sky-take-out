package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags="菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/list")
    @ApiOperation("根据类型id查询分类")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据类型查询分类");
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }


    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品");
        dishService.saveWithFlavor(dishDTO);

        String key = "dish_"+ dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }


    //分页查询菜品
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    //根据id删除特定菜品
    @DeleteMapping
    @ApiOperation("根据id删除特定菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品:{}",ids);
        dishService.deleteBatch(ids);

        //将所有菜品的缓存的数据都删除 所有以dish开头的key
        cleanCache("dish_*");

        return Result.success();
    }

    //根据id查询菜品数据
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品数据")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info ("根据id查询菜品数据:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    //修改菜品
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        cleanCache("dish_*");
        return Result.success();
    }

    //设置菜品是否起售
    @PostMapping("/status/{status}")
    @ApiOperation("设置菜品的起售停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("设置菜品启售停售:{}",id);
        dishService.startOrStop(status,id);

        cleanCache("dish_*");
        return Result.success();
    }


    //将Redis所有菜品数据清除
    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        if(keys != null && !keys.isEmpty()){
            redisTemplate.delete(keys);
        }
    }
}
