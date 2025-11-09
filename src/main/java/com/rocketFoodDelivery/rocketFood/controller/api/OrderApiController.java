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
    
    // POST /api/order/{order_id}/status endpoint
    // Path parameter: order id (required)
    // Returns the new status
    @PostMapping("/api/order/{order_id}/status")
    public ResponseEntity<Object> updateOrderStatus(
        @PathVariable("order_id") int orderId,
        @Valid @RequestBody ApiOrderStatusDTO apiOrderStatusDTO,
        BindingResult bindingResult) {
    
    if (bindingResult.hasErrors()) {
        throw new BadRequestException("Invalid or missing parameters");
    }
    
    ApiOrderStatusDTO updatedStatus = orderService.updateOrderStatus(orderId, apiOrderStatusDTO);
    return ResponseBuilder.buildDirectResponse(updatedStatus);
}

    // GET /api/orders
        // ● Returns a list of orders given a user type and id
        // ● Query parameter ‘type’: customer, restaurant or courier (required)
        // ● Query parameter ‘id’: id of customer, restaurant or courier (required) * Not the id of the
        // users table entry
        // ● Example: /api/orders?type=customer&id=7
   @GetMapping("/api/orders")
    public ResponseEntity<Object> getOrdersByUserTypeAndId(
        @RequestParam("type") String userType,
        @RequestParam("id") int userId) {
    
    List<ApiOrderDTO> orders = orderService.getOrdersByUserTypeAndId(userType, userId);
    if (orders.isEmpty()) {
        throw new ResourceNotFoundException("No orders found for " + userType + " with id " + userId);
    }
    return ResponseBuilder.buildOkResponse(orders);
}

    // POST /api/orders
    // Creates a new order

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

