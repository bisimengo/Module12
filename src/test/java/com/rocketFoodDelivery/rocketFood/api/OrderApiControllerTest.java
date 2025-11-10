package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.rocketFoodDelivery.rocketFood.exception.BadRequestException;

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

    @MockBean  
    private OrderService orderService;

    @Mock  // Keep this as @Mock
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    private ApiAddressDTO inputAddress;

    // @Test
    // public void testCreateOrder_Success() throws Exception {
    //     List<ApiCreateOrderDTO.ProductOrder> products = Arrays.asList(
    //         new ApiCreateOrderDTO.ProductOrder(2, 1),
    //         new ApiCreateOrderDTO.ProductOrder(3, 3)
    //     );
    //     ApiCreateOrderDTO inputOrder = new ApiCreateOrderDTO(1, 3, products);

    //     ApiOrderDTO mockResponse = new ApiOrderDTO();
    //     mockResponse.setId(1);
    //     mockResponse.setCustomerId(3);
    //     mockResponse.setRestaurantId(1);
    //     mockResponse.setStatus("pending");

    //     when(orderService.createOrder(any(ApiCreateOrderDTO.class))).thenReturn(mockResponse);

    //     mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(new ObjectMapper().writeValueAsString(inputOrder)))
    //             .andExpect(MockMvcResultMatchers.status().isOk())
    //             .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
    //             .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
    //             .andExpect(MockMvcResultMatchers.jsonPath("$.data.customer_id").value(3))
    //             .andExpect(MockMvcResultMatchers.jsonPath("$.data.restaurant_id").value(1));
    // }

    // @Test
    // public void testCreateOrder_BadRequest() throws Exception {
    //     // Invalid request body
    //     ApiCreateOrderDTO invalidOrder = new ApiCreateOrderDTO(0, 0, null);
        
    //     // Mock service to throw BadRequestException for invalid data
    //     when(orderService.createOrder(any(ApiCreateOrderDTO.class)))
    //         .thenThrow(new BadRequestException("Invalid or missing parameters"));
        
    //     mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(new ObjectMapper().writeValueAsString(invalidOrder)))
    //             .andExpect(MockMvcResultMatchers.status().isBadRequest())
    //             .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));
    // }
}
