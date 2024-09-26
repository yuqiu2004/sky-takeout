package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.properties.BaiduProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Resource
    private BaiduProperties baiduProperties;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){ // 地址为空异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 校验地址
        checkIfOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
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
        Page<Orders> ordersPage = orderMapper.list(ordersPageQueryDTO);
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

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception{
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        // 如果已付款 需要进行退款
        if(orders.getPayStatus().equals(Orders.PAY_STATUS_PAID)){
//            weChatPayUtil.refund(
//                    orders.getNumber(),
//                    orders.getNumber(),
//                    orders.getAmount(),
//                    orders.getAmount()
//            );
            orders.setPayStatus(Orders.PAY_STATUS_REFUND);
        }
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.STATUS_CANCELLED);
        orderMapper.update(orders);
    }

    @Override
    public OrderStatisticsVO statistic() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        // 依次统计 待接单、待派送、派送中
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.STATUS_TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.STATUS_CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.STATUS_DELIVERY_IN_PROGRESS);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if(null == orders || !orders.getStatus().equals(Orders.STATUS_DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.STATUS_COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getById(ordersRejectionDTO.getId());
        if(null == order || !order.getStatus().equals(Orders.STATUS_TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 如果已付款 需要进行退款
        if(order.getPayStatus().equals(Orders.PAY_STATUS_PAID)){
//            weChatPayUtil.refund(
//                    orders.getNumber(),
//                    orders.getNumber(),
//                    orders.getAmount(),
//                    orders.getAmount()
//            );
            order.setPayStatus(Orders.PAY_STATUS_REFUND);
        }
        order.setStatus(Orders.STATUS_CANCELLED);
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.STATUS_CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders order = orderMapper.getById(id);
        if(null == order || !order.getStatus().equals(Orders.STATUS_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.STATUS_DELIVERY_IN_PROGRESS);
        orderMapper.update(order);
    }

    @Override
    public PageResult search(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.list(ordersPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkIfOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",baiduProperties.getShopAddress());
        map.put("output","json");
        map.put("ak",baiduProperties.getAk());

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }
}
