package com.sky.mapper;

import com.sky.entity.SetMealDish;
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

    /**
     * 新增套餐绑定关系
     * @param setMealDishes
     */
    void insertBatch(List<SetMealDish> setMealDishes);

    /**
     * 根据套餐id解除关系
     * @param ids
     */
    void deleteBySetMealIds(List<Long> ids);

    /**
     * 根据套餐id获取关系
     * @param id
     * @return
     */
    @Select("select * from set_meal_dish where set_meal_id = #{id}")
    List<SetMealDish> getBySetMealId(Long id);
}
