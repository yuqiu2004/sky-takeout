package com.sky.service;

import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface SetMealService {

    /**
     * 分页查询套餐
     * @param dto
     */
    PageResult pageQuery(SetMealPageQueryDTO dto);

    /**
     * 新增套餐
     * @param setMealDTO
     */
    void add(SetMealDTO setMealDTO);

    /**
     * 批量删除
     * @param ids
     */
    void delete(List<Long> ids);
}