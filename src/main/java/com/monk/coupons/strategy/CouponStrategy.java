package com.monk.coupons.strategy;

import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.entity.Coupon;
import com.monk.coupons.entity.enums.CouponType;

public interface CouponStrategy {
    CouponType getType();
    double calculateDiscount(Coupon coupon, CartDTO cart);
    ApplyCouponResponse apply(Coupon coupon, CartDTO cart);
}
