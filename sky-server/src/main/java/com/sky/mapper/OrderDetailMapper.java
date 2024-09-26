package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 插入订单明细
     * @param details
     */
    void insertBatch(List<OrderDetail> details);

    /**
     * 根据订单id获取明细
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(Long id);
}
