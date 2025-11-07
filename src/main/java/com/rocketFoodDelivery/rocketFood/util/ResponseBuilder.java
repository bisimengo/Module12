package com.rocketFoodDelivery.rocketFood.util;

import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom utility class for handling API responses. Only manages success responses. Error responses
 * are managed by the {@link com.rocketFoodDelivery.rocketFood.controller.GlobalExceptionHandler} class
 */
public class ResponseBuilder {

    /**
     * Builds a success response with message and data wrapper (HTTP 200)
     */
    public static ResponseEntity<Object> buildOkResponse(Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Builds a created response with message and data wrapper (HTTP 201)
     */
    public static ResponseEntity<Object> buildCreatedResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Success");
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Builds a direct response without wrapping in message/data structure (HTTP 200)
     */
    public static ResponseEntity<Object> buildDirectResponse(Object data) {
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


}