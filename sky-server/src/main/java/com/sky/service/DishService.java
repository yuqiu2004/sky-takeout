package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 插入菜品
     * @param dishDTO
     */
    void addWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param pageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO pageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 修改菜品状态
     * @param id
     */
    void updateDishStatusById(Integer status, Long id);

    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    List<DishVO> list(Long categoryId);
}
