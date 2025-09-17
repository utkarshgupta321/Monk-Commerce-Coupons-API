package com.monk.coupons.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.entity.Coupon;
import com.monk.coupons.entity.enums.CouponType;
import org.springframework.stereotype.Component;

@Component
public class CartWiseStrategy implements CouponStrategy {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public CouponType getType() { return CouponType.CART_WISE; }

    @Override
    public double calculateDiscount(Coupon coupon, CartDTO cart) {
        try {
            JsonNode node = mapper.readTree(coupon.getDetailsJson());
            double threshold = node.get("threshold").asDouble();
            double discount = node.get("discount").asDouble(); // percentage
            double total = cart.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
            if (total > threshold) {
                return total * discount / 100.0;
            }
        } catch (Exception e) {
            // log in real app
        }
        return 0.0;
    }

    @Override
    public ApplyCouponResponse apply(Coupon coupon, CartDTO cart) {
        double discount = calculateDiscount(coupon, cart);
        double total = cart.getItems().stream().mapToDouble(i -> i.getPrice()*i.getQuantity()).sum();
        ApplyCouponResponse res = new ApplyCouponResponse();
        res.setItems(cart.getItems());
        res.setTotalPrice(total);
        res.setTotalDiscount(discount);
        res.setFinalPrice(total - discount);
        return res;
    }
}
