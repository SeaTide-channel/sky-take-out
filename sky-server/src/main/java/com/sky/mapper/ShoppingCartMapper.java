package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    //根据菜品id和用户id查询这个用户购物车内是否存在这个菜品
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    //用户新添加的菜品在购物车内有 所以number需要更新
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    //用户新添加的菜品插入购物车
    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" + "values  (#{name}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    //清除购物车内容
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    //根据id查询购物车内菜品
    @Select("select * from shopping_cart where id = #{id}")
    ShoppingCart getById(Long id);

    //根据id清除购物车内的菜品
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
