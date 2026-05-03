package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface DishFlavorMapper {

    @AutoFill(value = OperationType.INSERT)
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);




}
