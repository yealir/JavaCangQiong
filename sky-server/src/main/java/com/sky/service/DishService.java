package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    /**
     * 新增菜品 和 关联的口味
     */
    public void saveDishWithFlavor(DishDTO dishDTO);
}
