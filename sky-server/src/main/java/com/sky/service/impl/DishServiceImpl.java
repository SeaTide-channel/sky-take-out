package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.j2objc.annotations.AutoreleasePool;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    //根据类型id搜索菜品
    public List<Dish> list(Long categoryId){
        if(categoryId==null)return null;

        //将查询到的菜品返还
        List<Dish> dishes = dishMapper.list(categoryId);//查询该分类下的所有菜品
        return dishes != null? dishes : List.of();//避免返回null
    }


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

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //分页查询菜品数据 startPage(查询页面,查询的数量)
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);


        return new PageResult(page.getTotal(),page.getResult());
    }


    //批量删除菜品功能
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否起售中
        for (Long id : ids) {
            int status = dishMapper.getById(id).getStatus();
            if (status == 1){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否在套餐中
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            //起售中的菜品不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //批量删除菜品
        dishMapper.deleteBatch(ids);

        //删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishId(ids);
    }

    //根据菜品id搜索菜品数据
    public DishVO getByIdWithFlavor(Long id) {

        //查询菜品
        Dish dish=dishMapper.getById(id);
        //查询该菜品口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将dish与dishFlavors封装成DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    //修改菜品
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {


        //菜品数据更新
        Dish dish=new Dish();//创建Dish对象
        BeanUtils.copyProperties(dishDTO,dish);//拷贝数据
        dishMapper.update(dish);//更新菜品数据

        //进行口味的更新
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();//获取前端发来的菜品口味数据
        dishFlavorMapper.updateWithFlavor(dishFlavors);//批量更新菜品口味数据

    }
}
