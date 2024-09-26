package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号获取当前用户的订单
     * @param outTradeNo
     * @param userId
     * @return
     */
    @Select("select * from orders where number = #{outTradeNo} and user_id = #{userId}")
    Orders getByNumberAndUserId(String outTradeNo, Long userId);

    /**
     * 更新订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询订单列表
     * @param orders
     * @return
     */
    Page<Orders> list(Orders orders);

    /**
     * 根据id获取记录
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);
}
