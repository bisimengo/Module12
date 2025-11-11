package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductApiController {
    private ProductService productService;

    @Autowired
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }
    
    // GET /api/products?restaurant={restaurant_id}
    @GetMapping("/api/products")
    public ResponseEntity<Object> getProductsByRestaurantId(@RequestParam("restaurant") int restaurantId) {
        List<ApiProductDTO> products = productService.findProductsByRestaurantId(restaurantId);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Products for restaurant with id " + restaurantId + " not found");
        }
        return ResponseBuilder.buildOkResponse(products);
    }
}