package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 插入订单明细
     * @param details
     */
    void insertBatch(List<OrderDetail> details);
}
