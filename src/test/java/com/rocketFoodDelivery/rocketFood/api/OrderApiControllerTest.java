package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.controller.api.OrderApiController;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.service.OrderService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderApiControllerTest {
    @InjectMocks
    private OrderApiController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    private ApiAddressDTO inputAddress;

    @Test
    public void testCreateOrder_Success() throws Exception {
        // Use the correct inner class
        List<ApiCreateOrderDTO.ProductOrder> products = Arrays.asList(
            new ApiCreateOrderDTO.ProductOrder(2, 1),
            new ApiCreateOrderDTO.ProductOrder(3, 3)
        );
        ApiCreateOrderDTO inputOrder = new ApiCreateOrderDTO(1, 3, products);

        // Create mock response using default constructor
        ApiOrderDTO mockResponse = new ApiOrderDTO();
        mockResponse.setId(1);
        mockResponse.setCustomerId(3);
        mockResponse.setRestaurantId(1);
        mockResponse.setStatus("pending");
        
        // Fix the mock - check if your service returns Optional<ApiOrderDTO> or just ApiOrderDTO
        // Option 1: If service returns Optional<ApiOrderDTO>
        when(orderService.createOrder(any(ApiCreateOrderDTO.class))).thenReturn(mockResponse);
        
        // Option 2: If service returns ApiOrderDTO
        // when(orderService.createOrder(any(ApiCreateOrderDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(inputOrder)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.customer_id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.restaurant_id").value(1));
    }

    @Test
    public void testCreateOrder_BadRequest() throws Exception {
        // Invalid request body (missing required fields)
        ApiCreateOrderDTO invalidOrder = new ApiCreateOrderDTO(0, 0, null); // Invalid data

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidOrder)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isEmpty());
    }

}
