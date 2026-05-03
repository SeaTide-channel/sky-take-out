package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        //保存菜品数据
        Dish dish = new Dish();
        //前端发来的DTO数据转化为实体类
        BeanUtils.copyProperties(dishDTO,dish);
        //向数据库菜品表插入1条数据
        dishMapper.insert(dish);

        //获取新插入的菜品的id
        Long dishId = dish.getId();

        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();//获取前端发来的口味数据
        if (flavors != null && flavors.size() > 0){//菜品口味数据不为空
                flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));//为菜品口味数据添加菜品id
                dishFlavorMapper.insertBatch(flavors);//批量插入该菜品的口味类型
        }
    }
}
