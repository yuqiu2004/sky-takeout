package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.entity.SetMeal;
import com.sky.entity.SetMealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetMealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetMealMapper setMealMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    @Override
    public PageResult pageQuery(SetMealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(dto, setMeal);
        Page<SetMealVO> page = setMealMapper.queryWithCategoryName(setMeal);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(SetMealDTO setMealDTO) {
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(setMealDTO, setMeal);
        setMeal.setStatus(StatusConstant.DISABLE);
        setMealMapper.insert(setMeal);
        List<SetMealDish> dishes = setMealDTO.getSetMealDishes();//建立关系
        if(dishes != null && !dishes.isEmpty()){
            dishes.forEach(d->{
                d.setSetMealId(setMeal.getId());
                d.setName(setMeal.getName());
                d.setPrice(setMeal.getPrice());
            });
            setMealDishMapper.insertBatch(dishes);
        }
    }
}
