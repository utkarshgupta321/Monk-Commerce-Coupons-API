package com.monk.coupons.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.entity.Coupon;
import com.monk.coupons.entity.enums.CouponType;
import com.monk.coupons.repository.CouponRepository;
import com.monk.coupons.service.CouponService;
import com.monk.coupons.strategy.CouponStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CouponServiceImpl implements com.monk.coupons.service.CouponService {

    private final CouponRepository repo;
    private final ObjectMapper mapper;
    private final Map<CouponType, CouponStrategy> strategyMap;

    public CouponServiceImpl(CouponRepository repo, ObjectMapper mapper, List<CouponStrategy> strategies) {
        this.repo = repo;
        this.mapper = mapper;
        this.strategyMap = new HashMap<>();
        strategies.forEach(s -> this.strategyMap.put(s.getType(), s));
    }

    @Override
    public Coupon create(Coupon coupon) { return repo.save(coupon); }

    @Override
    public List<Coupon> getAll() { return repo.findAll(); }

    @Override
    public Coupon getById(Long id) { return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Coupon not found")); }

    @Override
    public Coupon update(Long id, Coupon coupon) {
        Coupon e = getById(id);
        e.setDetailsJson(coupon.getDetailsJson());
        e.setActive(coupon.isActive());
        e.setExpiryDate(coupon.getExpiryDate());
        e.setType(coupon.getType());
        e.setCode(coupon.getCode());
        return repo.save(e);
    }

    @Override
    public void delete(Long id) { repo.deleteById(id); }

    private boolean isCouponValid(Coupon c) {
        if (!c.isActive()) return false;
        if (c.getExpiryDate() != null && c.getExpiryDate().isBefore(LocalDate.now())) return false;
        return true;
    }

    @Override
    public List<CouponService.ApplicableCouponDTO> getApplicableCoupons(CartDTO cart) {
        List<Coupon> coupons = repo.findAll();
        List<CouponService.ApplicableCouponDTO> results = new ArrayList<>();
        for (Coupon c : coupons) {
            if (!isCouponValid(c)) continue;
            CouponStrategy strategy = strategyMap.get(c.getType());
            if (strategy == null) continue;
            double disc = strategy.calculateDiscount(c, cart);
            if (disc > 0) {
                CouponService.ApplicableCouponDTO dto = new CouponService.ApplicableCouponDTO();
                dto.couponId = c.getId();
                dto.type = c.getType().name();
                dto.discount = disc;
                results.add(dto);
            }
        }
        return results;
    }

    @Override
    public ApplyCouponResponse applyCoupon(Long couponId, CartDTO cart) {
        Coupon coupon = getById(couponId);
        if (!isCouponValid(coupon)) throw new IllegalArgumentException("Coupon expired or inactive");
        CouponStrategy strategy = strategyMap.get(coupon.getType());
        if (strategy == null) throw new IllegalArgumentException("Unsupported coupon type");
        return strategy.apply(coupon, cart);
    }

    @Override
    public ApplyCouponResponse applyBestCoupon(CartDTO cart) {
        List<CouponService.ApplicableCouponDTO> applicable = getApplicableCoupons(cart);
        if (applicable.isEmpty()) {
            ApplyCouponResponse res = new ApplyCouponResponse();
            double total = cart.getItems().stream().mapToDouble(i -> i.getPrice()*i.getQuantity()).sum();
            res.setItems(cart.getItems());
            res.setTotalPrice(total);
            res.setTotalDiscount(0.0);
            res.setFinalPrice(total);
            return res;
        }
        // choose coupon with max discount
        CouponService.ApplicableCouponDTO best = applicable.stream().max(Comparator.comparingDouble(a -> a.discount)).get();
        return applyCoupon(best.couponId, cart);
    }
}
