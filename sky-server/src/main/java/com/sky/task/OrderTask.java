package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 自定义定时任务 订单状态定时处理
 */
@Component
@Slf4j
public class OrderTask {

    @Resource
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理支付超时订单：{}", new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.STATUS_PENDING_PAYMENT, time);
        if(null != ordersList && !ordersList.isEmpty()){
            ordersList.forEach( orders -> {
                orders.setStatus(Orders.STATUS_CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason(Orders.CANCEL_REASON_PAY_TIMEOUT);
                orderMapper.update(orders);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.STATUS_DELIVERY_IN_PROGRESS, time);
        if(null != ordersList && !ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.STATUS_COMPLETED);
                orderMapper.update(orders);
            });
        }
    }
}
