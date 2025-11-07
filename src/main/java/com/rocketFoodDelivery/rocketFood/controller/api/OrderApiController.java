package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDTO;
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
            ApiErrorDTO apiErrorDTO = ResponseBuilder.buildValidationErrorResponse(bindingResult);
            return ResponseBuilder.buildBadRequestResponse(apiErrorDTO);
        }
        try {
            ApiOrderStatusDTO updatedStatus = orderService.updateOrderStatus(orderId, apiOrderStatusDTO);
            return ResponseBuilder.buildOkResponse(updatedStatus);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        } catch (InvalidStatusTransitionException e) {
            throw new InvalidStatusTransitionException("Invalid status transition: " + e.getMessage());
        }
    }

    // POST /api/orders
    // Creates a new order

    // @PostMapping("/api/orders")
    // public ResponseEntity<Object> createOrder(
    //         @Valid @RequestBody ApiCreateOrderDTO apiCreateOrderDTO,
    //         BindingResult bindingResult) {
    //     if (bindingResult.hasErrors()) {
    //         ApiErrorDTO apiErrorDTO = ResponseBuilder.buildValidationErrorResponse(bindingResult);
    //         return ResponseBuilder.buildBadRequestResponse(apiErrorDTO);
    //     }
    //     try {
    //         ApiOrderDTO createdOrder = orderService.createOrder(apiCreateOrderDTO);
    //         return ResponseBuilder.buildOkResponse(createdOrder);
    //     } catch (ResourceNotFoundException e) {
    //         throw new ResourceNotFoundException("Resource not found: " + e.getMessage());
    //     } catch (InsufficientInventoryException e) {
    //         throw new InsufficientInventoryException("Insufficient inventory: " + e.getMessage());
    //     } catch (Exception e) {
    //         throw new RuntimeException("Error creating order: " + e.getMessage());
    //     }
    //     return ResponseBuilder.buildInternalServerErrorResponse("Error creating order");
    // }


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
        try {
            return ResponseBuilder.buildOkResponse(orderService.getOrdersByUserTypeAndId(userType, userId));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("No orders found for " + userType + " with id " + userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage());
        }
    }   


}