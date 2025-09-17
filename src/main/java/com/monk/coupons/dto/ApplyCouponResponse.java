package com.monk.coupons.dto;

import lombok.Data;
import java.util.List;

@Data
public class ApplyCouponResponse {
    private List<CartItemDTO> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
}
