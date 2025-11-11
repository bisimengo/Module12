package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiProductForOrderApiDTO {
    @JsonProperty("product_id")
    private int productId;    
     
    @JsonProperty("product_quantity")
    private int productQuantity;
    
    @JsonProperty("unit_cost")
    private int unitCost;
    
    @JsonProperty("total_cost")
    private int totalCost;
}
