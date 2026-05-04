package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper

public interface DishFlavorMapper {

    //批量插入菜品的口味数据
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);

    //批量删除菜品口味数据
    void deleteByDishId(List<Long> ids);

    //根据菜品id搜索口味
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long id);

    //批量更新菜品口味数据
    void updateWithFlavor(List<DishFlavor> dishFlavors);
}
