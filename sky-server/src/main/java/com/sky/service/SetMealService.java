package com.sky.service;

import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetMealVO;

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

    /**
     * 更新套餐
     * @param setMealDTO
     */
    void update(SetMealDTO setMealDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    void status(Integer status, Long id);

    /**
     * 根据id获取套餐信息
     * @param id
     * @return
     */
    SetMealVO getById(String id);
}
