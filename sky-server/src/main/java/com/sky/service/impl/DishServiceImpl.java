package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
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
    private SetmealMapper setmealMapper;
    @Transactional
    public void saveDishWithFlavor(DishDTO dishDTO) {
        //向菜品插入一条数据 属性命名一致
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        //获取插入后的菜品ID 自增id 需要在xml文件中配置
        Long dishId = dish.getId();
        //插入多条口味数据，这里数据库中逻辑外键代码维护 口味中有关联的菜品的主键id 所以需要在插入菜品后，将id回赋值回来，然后在变量所有的口味将id插入进去
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && dishFlavors.size() > 0){
            //Lambda表达式 设置菜品ID
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //口味存在 才插入
            dishFlavorMapper.insertFlavorBatch(dishFlavors);
        }
    }


    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }



    @Transactional // 多个数据库操作，增加事务
    public void deleteDish(List<Long> ids) {
        //遍历每个id 看是否正在售卖，正在售卖的菜品无法删除
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //遍历每个id 是否关联了套餐，套餐有也不能直接删除
        List<Long> setmealIds = setmealMapper.getSetmealIdByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //根据id删除菜品
        for(Long id : ids){
            dishMapper.deleteDishByDishId(id);
            dishFlavorMapper.deleteFlavorByDishId(id);
        }

    }
}
