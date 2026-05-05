

package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    //新增套餐
    @PostMapping
    @ApiOperation("新增套餐接口")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐");
        //调用service新增套餐
        setmealService.insert(setmealDTO);

        return Result.success();
    }

    //套餐分页查询
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询");
        //调用service分页查询
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    //根据发送过来的id组合删除套餐
    @DeleteMapping
    @ApiOperation("批量删除套餐接口")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}",ids);
        setmealService.deleteWithSetmealDish(ids);
        return Result.success();
    }

    //根据id查询套餐
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐接口")
    public Result<SetmealDTO> getById(@PathVariable Long id){
        log.info("根据id查询套餐");
        SetmealDTO setmealDTO = setmealService.getById(id);
        return Result.success(setmealDTO);
    }

    //设置套餐起售
    @PostMapping("/status/{status}")
    @ApiOperation("设置套餐起售接口")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("设置套餐起售");
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    //修改套餐数据
    @PutMapping
    @ApiOperation("修改套餐数据接口")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐数据");
        setmealService.update(setmealDTO);
        return Result.success();
    }

}
