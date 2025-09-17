package com.monk.coupons.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.dto.ApplyCouponResponse;
import com.monk.coupons.dto.CartDTO;
import com.monk.coupons.entity.Coupon;
import com.monk.coupons.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController {
	
	 @GetMapping
	 public String welcome() {
	     return "Welcome to the Monk Coupon API";
	 }
	
    private final CouponService service;
    private final ObjectMapper mapper;

    public CouponController(CouponService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // ----------------- CRUD ENDPOINTS -----------------

    @PostMapping("/coupons")
    public ResponseEntity<Coupon> create(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(service.create(coupon));
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<Coupon> one(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<Coupon> update(@PathVariable Long id, @Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(service.update(id, coupon));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------- BUSINESS ENDPOINTS -----------------

    /**
     * Get all applicable coupons for a given cart
     */
    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<CouponService.ApplicableCouponDTO>> applicable(
            @Valid @RequestBody CartDTO cart) {
        return ResponseEntity.ok(service.getApplicableCoupons(cart));
    }

    /**
     * Apply a specific coupon to the cart
     */
    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<ApplyCouponResponse> apply(
            @PathVariable Long id,
            @Valid @RequestBody CartDTO cart) {
        return ResponseEntity.ok(service.applyCoupon(id, cart));
    }

    /**
     * Apply the best coupon (highest discount) automatically
     */
    @PostMapping("/apply-best-coupon")
    public ResponseEntity<ApplyCouponResponse> applyBest(@Valid @RequestBody CartDTO cart) {
        return ResponseEntity.ok(service.applyBestCoupon(cart));
    }
}
