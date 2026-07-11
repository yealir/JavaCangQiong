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
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //将套餐数据保存到套餐表
        setmealMapper.insert(setmeal);

        //获取套餐自增的主键id
        Long setmealId = setmeal.getId();
        //将关联的菜品添加到关联表中
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insertSetmealDish(setmealDishes);
        }
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {

        //先把索引和页码处理
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> setmealPage = setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 删除套餐
     * @param ids
     */
    public void delete(List<Long> ids) {
        //通过套餐id 拿到所有的套餐状态
        for(Long id : ids){
            Setmeal setmeal = new Setmeal();
            setmeal = setmealMapper.getSetmealById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                //定义的异常类 会被全局异常处理器，handler 进行捕获处理
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //所有的都可以删除 就都删除
        for(Long id : ids){
            setmealMapper.deleteById(id);
        }

    }

    /**
     * 根据 套餐id 查询套餐
     * @param id
     * @return
     */
    public SetmealVO getsetmealById(Long id) {

        //套餐和相关联的dish
        //先把setmeal查出来然后根据setmealid 去setmeal_dish关联表差所有关联的菜品

        Setmeal setmeal = setmealMapper.getSetmealById(id);

        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishById(setmeal.getId());

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //分为两步，更新套餐表 ， 更新套餐关联餐品表

        //拷贝出setmeal实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        if(setmealMapper.getSetmealById(setmeal.getId()).getStatus() == StatusConstant.ENABLE){
            new SetmealEnableFailedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //
        setmealMapper.update(setmeal);

        //直接删除 关联的菜品，然后插入就可以
        setmealDishMapper.delete(setmeal.getId());
        Long setmealId = setmeal.getId();
        //插入 因为setmealId 前端不一定穿过来所以要自己在实体里面设定，不然就无法通过setmealId进行删除了
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insertSetmealDish(setmealDishes);

    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    public void updateStatus(Integer status, Long id) {
        //修改，只有在套餐选定的这些菜品都是在售状态才能让整个套餐都在售
        // 首先根据
        if(status == StatusConstant.ENABLE){
            List<Dish> dishList = dishMapper.getDishListBySetmealId(id);
            if(dishList != null && dishList.size() > 0)
                for(Dish dish : dishList){
                    if(dish.getStatus() == StatusConstant.DISABLE)
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
        }
        Setmeal setmeal = Setmeal.builder()
                        .id(id).status(status).build();
        setmealMapper.update(setmeal);

    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
