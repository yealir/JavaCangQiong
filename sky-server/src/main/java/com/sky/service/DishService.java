package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品 和 关联的口味
     */
    public void saveDishWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     *根据菜品id删除菜品
     * @param ids
     */
    void deleteDish(List<Long> ids);

    DishVO getDishByIdWithFlavor(Long id);

    void updateDishWithFlavor(DishDTO dishDTO);

    List<Dish> list(Long categoryId);
}
