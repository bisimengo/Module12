package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiCreateOrderDTO {
    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Integer restaurantId;
    
    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Integer customerId;
    
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private List<ProductOrderDTO> products;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductOrderDTO {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Integer id;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }
}