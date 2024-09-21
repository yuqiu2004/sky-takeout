package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id获取绑定的套餐id
     * @param ids
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> ids);
}
