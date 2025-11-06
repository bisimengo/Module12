package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiCreateRestaurantDTO {
    private Integer id;           
    private String name;
    private Integer userId;
    private ApiAddressDTO address;  
    private Integer priceRange;
    private String phone; 
    private String email;


    // Keep only the custom setter for JSON mapping
    @JsonProperty("user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
