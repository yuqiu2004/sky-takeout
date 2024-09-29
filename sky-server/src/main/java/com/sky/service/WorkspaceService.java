package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;

public interface WorkspaceService {

    /**
     * 今日数据
     * @return
     */
    BusinessDataVO businessData();

    /**
     * 订单浏览
     * @return
     */
    OrderOverViewVO overviewOrders();
}
