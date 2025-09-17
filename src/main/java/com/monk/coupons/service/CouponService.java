package com.monk.coupons.service;

import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.entity.Coupon;

import java.util.List;

public interface CouponService {
    Coupon create(Coupon coupon);
    List<Coupon> getAll();
    Coupon getById(Long id);
    Coupon update(Long id, Coupon coupon);
    void delete(Long id);

    List<ApplicableCouponDTO> getApplicableCoupons(CartDTO cart);
    ApplyCouponResponse applyCoupon(Long couponId, CartDTO cart);
    ApplyCouponResponse applyBestCoupon(CartDTO cart);

    class ApplicableCouponDTO {
        public Long couponId;
        public String type;
        public double discount;
    }
}
