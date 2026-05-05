package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.util.List;

public interface SetmealService {

    //新增套餐功能
    void insert(SetmealDTO setmealDTO);

    //根据id查询套餐
    SetmealDTO getById(Long id);

    //分页查询套餐功能
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    //批量删除套餐功能
    void deleteWithSetmealDish(List<Long> ids);

    //开关套餐是否起售功能
    void startOrStop(Integer status, Long id);

    //修改套餐数据
    void update(SetmealDTO setmealDTO);
}
