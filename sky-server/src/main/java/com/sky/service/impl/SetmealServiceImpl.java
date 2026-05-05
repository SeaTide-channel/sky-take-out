package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.w3c.dom.ls.LSInput;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    //新增菜品
    @Transactional
    public void insert(SetmealDTO setmealDTO) {

        //创建套餐对象 将DTO数值转换为实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //插入套餐数据需要同时操作两个表（setmeal表和setmeal_dish表）
        //插入套餐数据
        setmealMapper.insert(setmeal);

        // 获取生成的套餐ID 用于注入关联套餐的菜品
        Long setmealId = setmeal.getId();


        //套餐中的菜品获取
       List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes() ;

       if(setmealDishes != null && setmealDishes.size() > 0){
           setmealDishes.forEach(dish -> {
               dish.setSetmealId(setmealId);
           });
           //批量插入套餐菜品数据
           setmealDishMapper.insertBatch(setmealDishes);
       }else{
           log.warn("套餐中菜品数据为空");
       }
    }

    //分页查询套餐
    @Transactional
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        //查询分页数据
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        //查询套餐中的菜品数据并拼接套餐数据
        return new PageResult(page.getTotal(), page.getResult());
    }

    //批量删除套餐
    @Transactional
    public void deleteWithSetmealDish(List<Long> ids) {

        //删除套餐中表的参数
        setmealMapper.deleteBatch(ids);

        //删除套餐中的菜品数据
        setmealDishMapper.deleteBatch(ids);
    }

    //根据id查询套餐
    public SetmealDTO getById(Long id) {
        log.info("根据ID查询套餐: id={}", id);
        //创建DTO获取套餐数据(不包括套餐内的菜品)
        SetmealDTO setmealDTO = setmealMapper.getById(id);
        log.info("查询到的套餐基本信息: {}", setmealDTO);

        //获取套餐内菜品数据
        if(setmealDTO !=null){
           List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealById(id);
           setmealDTO.setSetmealDishes(setmealDishes);
        }

        log.info("========== 最终返回的套餐数据: {} ==========", setmealDTO);
        return setmealDTO;
    }

    //设置套餐起售
    public void startOrStop(Integer status, Long id) {
        if(Objects.equals(status, StatusConstant.DISABLE)){
            List<Dish> dishList = dishMapper.getSetmealById(id);
            if( dishList != null && dishList.size() > 0 ){
                for (Dish dish : dishList){
                    if(Objects.equals(dish.getStatus(), StatusConstant.DISABLE))throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        //修改数据接口与起售接口同步共享一个update
            //update需要setmeal的类型 因此创建一个Setmeal对象 用于上传到update
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        //调用修改接口
        setmealMapper.update(setmeal);
    }

    //修改套餐数据
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //创建Setmeal对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        //删除旧的套餐关系
        Long setmealId = setmealDTO.getId();
        if(setmealId!=null) {
            setmealDishMapper.deleteSetmealById(setmealId);

            List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
            if(setmealDishes != null && setmealDishes.size() > 0){
                for (SetmealDish setmealDish : setmealDishes ) setmealDish.setSetmealId(setmealId);
                setmealDishMapper.insertBatch(setmealDishes);
            }
        }
    }
}
