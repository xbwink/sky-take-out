package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-14 17:06
 * @vesion 1.0
 */
public interface OrderService {

    /**
     * 用户下单
     * @param dto
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO dto);
}
