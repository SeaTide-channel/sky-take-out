package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    //插入新菜品数据
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    //菜品分页查询
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据id批量删除菜品
    void deleteBatch(List<Long> ids);

    //根据id查询菜品
    @Select("SELECT * FROM dish WHERE id = #{id};")
    Dish getById(Long id);

    //修改菜品
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    //根据id搜索菜品类别 该菜品必须启售
    @Select("SELECT * FROM dish WHERE category_id = #{categoryId} and status = 1")
    List<Dish> list(Dish categoryId);

    //用户端根据套餐id查询套餐内的菜品
    @Select("SELECT * FROM dish WHERE id = #{id};")
    List<Dish> getSetmealById(Long id);
}
