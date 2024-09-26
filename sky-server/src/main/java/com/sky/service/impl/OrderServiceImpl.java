package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WeChatPayUtil weChatPayUtil;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){ // 地址为空异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(ShoppingCart.builder().userId(userId).build());
        if(null == shoppingCarts || shoppingCarts.isEmpty()){ // 购物车为空
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setStatus(Orders.STATUS_PENDING_PAYMENT);
        order.setPayStatus(Orders.PAY_STATUS_UN_PAID);
        order.setOrderTime(LocalDateTime.now());
        // 插入数据 需要返回id
        orderMapper.insert(order);
        // 生成订单明细
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(order.getId());
            details.add(orderDetail);
        }
        // 插入详细
        orderDetailMapper.insertBatch(details);
        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        // 封装返回值
        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
        return submitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "雨丘微商外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);
        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.STATUS_TO_BE_CONFIRMED)
                .payStatus(Orders.PAY_STATUS_PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    @Override
    public PageResult history(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Orders orders = Orders.builder().userId(BaseContext.getCurrentId()).status(ordersPageQueryDTO.getStatus()).build();
        Page<Orders> ordersPage = orderMapper.list(orders);
        List<Orders> result = ordersPage.getResult();
        List<OrderVO> list = new ArrayList<>();
        for (Orders order : result) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            List<OrderDetail> details = orderDetailMapper.getByOrderId(order.getId());
            orderVO.setOrderDetailList(details);
            list.add(orderVO);
        }
        return new PageResult(ordersPage.getTotal(), list);
    }

    @Override
    public OrderVO detail(Long id) {
        OrderVO orderVO = new OrderVO();
        // 获取订单的基本信息
        Orders order = orderMapper.getById(id);
        BeanUtils.copyProperties(order, orderVO);
        // 获取明细
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(details);
        return orderVO;
    }

    @Override
    public void cancel(Long id) throws Exception {
        Orders orders = orderMapper.getById(id);
        // 如果已经接单 抛出异常
        if(orders.getStatus() > Orders.STATUS_TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 如果已付款 需要进行退款
        if(orders.getStatus().equals(Orders.STATUS_TO_BE_CONFIRMED)){
//            weChatPayUtil.refund(
//                    orders.getNumber(),
//                    orders.getNumber(),
//                    orders.getAmount(),
//                    orders.getAmount()
//            );
            orders.setPayStatus(Orders.PAY_STATUS_REFUND);
        }
        orders.setCancelReason(Orders.CANCEL_REASON_USER_CANCEL);
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.STATUS_CANCELLED);
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        Long currentId = BaseContext.getCurrentId();
        Orders preOrder = orderMapper.getById(id);
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);
        // 将订单信息复制到购物车中
        List<ShoppingCart> shoppingCarts = details.stream().map(d -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(d, shoppingCart, "id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(currentId);
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertBatch(shoppingCarts);

    }
}
