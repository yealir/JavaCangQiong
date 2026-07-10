package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/setmeal")
@RestController
@Slf4j
public class SetmealContorller {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增套餐
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);

        return Result.success("新增套餐成功");
    }


    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐：{}", ids);
        setmealService.delete(ids);
        return Result.success("删除套餐成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> setmealQuery(@PathVariable Long id){
        log.info("根据套餐id查询套餐：{}" , id);

        SetmealVO setmealVO = new SetmealVO();
        setmealVO =  setmealService.getsetmealById(id);

        return Result.success(setmealVO);
    }

    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐信息：{}", setmealDTO);

        setmealService.update(setmealDTO);

        return Result.success();
    }


    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status ,Long id){
        log.info("修改套餐状态id为：{}",id);

        setmealService.updateStatus(status,id);

        return Result.success();
    }
}
