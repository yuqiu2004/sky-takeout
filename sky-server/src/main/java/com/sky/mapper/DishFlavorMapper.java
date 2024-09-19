package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味记录
     * @param flavors
     */
//    @AutoFill(value = OperationType.INSERT) 注意这里对应的表没有createTime之类的字段
    void insertBatch(List<DishFlavor> flavors);
}
