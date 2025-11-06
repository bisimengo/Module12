package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductForOrderApiDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDTO;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ProductApiController {
    private ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Returns a list of products for a given restaurant (GET)
     * query param is restaurant:id of the target restaurant
     * /api/products?restaurant=1
     *
     * @param restaurantId The ID of the restaurant to retrieve products for.
     * @return A list of products for the specified restaurant.
     */
    @GetMapping("/api/products")
    public ResponseEntity<Object> getProductsByRestaurantId(@RequestParam("restaurant") int restaurantId) {
        try {
            return ResponseBuilder.buildOkResponse(productService.findProductsByRestaurantId(restaurantId));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Products for restaurant with id " + restaurantId + " not found");
        }
    }
}