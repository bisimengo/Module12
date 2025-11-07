package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiOrderDTO {
    private int id;
    
    @JsonProperty("customer_id")
    private int customerId;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("customer_address")
    private String customerAddress;
    
    @JsonProperty("restaurant_id")
    private int restaurantId;
    
    @JsonProperty("restaurant_name")
    private String restaurantName;
    
    @JsonProperty("restaurant_address")
    private String restaurantAddress;
    
    @JsonProperty("courier_id")
    private Integer courierId; // Nullable
    
    @JsonProperty("courier_name")
    private String courierName;
    
    private String status;
    
    private List<ApiProductForOrderApiDTO> products;
    
    @JsonProperty("total_cost")
    private long totalCost;
}
