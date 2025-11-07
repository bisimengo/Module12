package com.rocketFoodDelivery.rocketFood.util;

import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDTO;

import org.springframework.http.HttpStatus;
import java.util.HashMap;

/**
 * Custom utility class for handling API responses. Only manages success responses. Error responses
 * are managed by the {@link com.rocketFoodDelivery.rocketFood.controller.GlobalExceptionHandler} class
 */

public class ResponseBuilder {

    public static ResponseEntity<Object> buildOkResponse(Object data) {
        ApiResponseDTO response = new ApiResponseDTO();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Object> buildCreatedResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Success");
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}