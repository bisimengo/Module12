package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDTO;  
import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDTO;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
public class OrderApiController {
    private OrderService orderService;

    @Autowired
    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    // POST /api/order/{order_id}/status 
    
    @PostMapping("/api/order/{order_id}/status")
        public ResponseEntity<Object> updateOrderStatus(
            @PathVariable("order_id") int orderId,
            @RequestBody ApiOrderStatusDTO apiOrderStatusDTO) {
        
        ApiOrderStatusDTO updatedStatus = orderService.updateOrderStatus(orderId, apiOrderStatusDTO);
        return ResponseBuilder.buildDirectResponse(updatedStatus);
}

    // GET /api/orders
        
   @GetMapping("/api/orders")
    public ResponseEntity<Object> getOrdersByUserTypeAndId(
        @RequestParam(value = "type", required = false) String userType,
        @RequestParam(value = "id", required = false) String userIdStr) {

    if (userType == null || userType.trim().isEmpty()) {
        throw new BadRequestException("Invalid or missing parameters");
    }
    
    if (userIdStr == null) {
        throw new BadRequestException("Invalid or missing parameters");
    }
    
    if (!userType.equals("customer") && !userType.equals("restaurant") && !userType.equals("courier")) {
        throw new BadRequestException("Invalid or missing parameters");
    }
    
    int userId;
    try {
        userId = Integer.parseInt(userIdStr);
        if (userId <= 0) {
            throw new BadRequestException("Invalid or missing parameters");
        }
    } catch (NumberFormatException e) {
        throw new BadRequestException("Invalid or missing parameters");
    }

    List<ApiOrderDTO> orders = orderService.getOrdersByUserTypeAndId(userType, userId);
    if (orders.isEmpty()) {
        throw new ResourceNotFoundException("No orders found for " + userType + " with id " + userId);
    }
    return ResponseBuilder.buildOkResponse(orders);
}

    // POST /api/orders

   @PostMapping("/api/orders")
    public ResponseEntity<Object> createOrder(
        @Valid @RequestBody ApiCreateOrderDTO apiCreateOrderDTO,
        BindingResult bindingResult) {
    
    if (bindingResult.hasErrors()) {
        throw new BadRequestException("Invalid or missing parameters");
    }    
    ApiOrderDTO createdOrder = orderService.createOrder(apiCreateOrderDTO);
    return ResponseBuilder.buildOkResponse(createdOrder);
}

}

