package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.entity.SetMeal;
import com.sky.entity.SetMealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.utils.MinioUtil;
import com.sky.vo.SetMealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetMealMapper setMealMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    @Resource
    private MinioUtil minioUtil;

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
        List<SetMealDish> dishes = setMealDTO.getSetmealDishes();//建立关系
        if(dishes != null && !dishes.isEmpty()){
            dishes.forEach(d->{
                d.setSetMealId(setMeal.getId());
                d.setName(setMeal.getName());
                d.setPrice(setMeal.getPrice());
            });
            setMealDishMapper.insertBatch(dishes);
        }
    }

    @Override
    public void delete(List<Long> ids) {
        // 发售的套餐不能删除
        List<SetMeal> list = setMealMapper.selectByIds(ids);
        for (SetMeal setMeal : list) {
            if(setMeal.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 删除套餐
        setMealMapper.deleteBatch(ids);
        // 删除对应的套餐图片
        for (SetMeal setMeal : list) {
            String image = setMeal.getImage();
            minioUtil.removeByPreUrl(image);
        }
        // 删除关系
        setMealDishMapper.deleteBySetMealIds(ids);
    }

    @Override
    public void update(SetMealDTO setMealDTO) {
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(setMealDTO, setMeal);
        setMealMapper.update(setMeal);
        List<SetMealDish> setmealDishes = setMealDTO.getSetmealDishes();
        if(null != setmealDishes && !setmealDishes.isEmpty()){
            // 删除原来的关联
            ArrayList<Long> longs = new ArrayList<>();
            longs.add(setMeal.getId());
            setMealDishMapper.deleteBySetMealIds(longs);
            // 插入新的关联
            setMealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public void status(Integer status, Long id) {
        setMealMapper.updateStatus(status, id);
    }
}
