package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
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
}
