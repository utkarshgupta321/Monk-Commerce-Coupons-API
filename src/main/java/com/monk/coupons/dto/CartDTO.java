package com.monk.coupons.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    @NotEmpty(message = "Cart must contain at least one item")
    @Valid
    private List<CartItemDTO> items;
}
