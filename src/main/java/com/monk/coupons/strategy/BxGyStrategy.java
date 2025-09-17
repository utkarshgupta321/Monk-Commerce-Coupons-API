package com.monk.coupons.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.dto.CartItemDTO;
import com.monk.coupons.entity.Coupon;
import com.monk.coupons.entity.enums.CouponType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BxGyStrategy implements CouponStrategy {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public CouponType getType() { return CouponType.BXGY; }

    @Override
    public double calculateDiscount(Coupon coupon, CartDTO cart) {
        try {
            JsonNode node = mapper.readTree(coupon.getDetailsJson());
            JsonNode buyArr = node.get("buy_products");
            JsonNode getArr = node.get("get_products");
            int repetitionLimit = node.has("repetition_limit") ? node.get("repetition_limit").asInt() : 1;

            Map<Long,Integer> buyReq = new HashMap<>();
            for (JsonNode b : buyArr) {
                buyReq.put(b.get("product_id").asLong(), b.get("quantity").asInt());
            }
            Map<Long,Integer> getReq = new HashMap<>();
            for (JsonNode g : getArr) {
                getReq.put(g.get("product_id").asLong(), g.get("quantity").asInt());
            }

            Map<Long,Integer> cartQty = cart.getItems().stream()
                    .collect(Collectors.toMap(CartItemDTO::getProductId, CartItemDTO::getQuantity, Integer::sum));

            int totalBuyReq = buyReq.values().stream().mapToInt(Integer::intValue).sum();
            int totalAvailableBuy = buyReq.keySet().stream().mapToInt(pid -> cartQty.getOrDefault(pid,0)).sum();
            if (totalBuyReq == 0) return 0.0;
            int reps = Math.min(repetitionLimit, totalAvailableBuy / totalBuyReq);
            if (reps <= 0) return 0.0;

            double totalDiscount = 0.0;
            for (CartItemDTO it : cart.getItems()) {
                if (getReq.containsKey(it.getProductId())) {
                    int available = it.getQuantity();
                    int perItemFree = getReq.get(it.getProductId()) * reps;
                    int freeCount = Math.min(available, perItemFree);
                    totalDiscount += freeCount * it.getPrice();
                }
            }
            return totalDiscount;
        } catch (Exception e) {
            e.printStackTrace();
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
