package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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
    Page<Orders> list(OrdersPageQueryDTO orders);

    /**
     * 根据id获取记录
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 根据状态和下单时间查询订单
     * @param status
     * @param orderTime
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 获取指定时间范围内的金额
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select sum(amount) from orders where status = #{status} and order_time >= #{beginTime} and order_time <= #{endTime}")
    Double getSumDuring(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 统计订单数
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Integer countOrder(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 获取销量前十的商品
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
