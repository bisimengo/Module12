package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiOrderRequestDTO {
    private Integer customerId;
    private Integer restaurantId;
    private Integer courierId;
    private List<ProductOrderRequestDTO> products;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductOrderRequestDTO {
        private Integer productId;
        private Integer quantity;
    }
}
