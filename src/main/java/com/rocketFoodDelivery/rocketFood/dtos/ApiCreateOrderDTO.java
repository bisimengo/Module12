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
    private Integer id; 
    private Integer restaurantId;          
    private String customerId;
    private Integer orderStatus;
    private ApiAddressDTO courier;  
    private Integer restaurantRating;
   

    
    // Custom setters for JSON mapping
    @JsonProperty("customer_id")
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    @JsonProperty("restaurant_id")
    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    @JsonProperty("courier_id")
    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }
}