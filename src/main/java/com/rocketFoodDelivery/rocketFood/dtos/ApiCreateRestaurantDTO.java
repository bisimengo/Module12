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
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Integer userId;
    
    @NotNull(message = "Address is required")
    @Valid
    private ApiAddressDTO address;  
    
    @NotNull(message = "Price range is required")
    @Min(value = 1, message = "Price range must be between 1 and 3")
    @Max(value = 3, message = "Price range must be between 1 and 3")
    @JsonProperty("price_range")
    private Integer priceRange;
    
    @NotBlank(message = "Phone is required")
    private String phone; 
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
