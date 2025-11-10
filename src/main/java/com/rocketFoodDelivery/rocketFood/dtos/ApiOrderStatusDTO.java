package com.rocketFoodDelivery.rocketFood.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiOrderStatusDTO {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(pending|in_progress|delivered|cancelled)$", 
             message = "Status must be one of: pending, in progress, delivered")
    private String status;
}
