package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    //根据菜品id查询套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    //批量插入套餐菜品数据
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);

    //批量删除套餐菜品数据
    void deleteBatch(List<Long> ids);

    //根据套餐id查询套餐菜品数据
    @Select("select * from setmeal_dish where setmeal_id = #{id} order by copies")
    List<SetmealDish> getSetmealById(Long id);

    //根据id删除旧的套餐关系
    @AutoFill(value = OperationType.DELETE)
    void deleteSetmealById(Long setmealId);


}
