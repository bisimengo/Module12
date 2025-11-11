package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock  
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    private ApiAddressDTO inputAddress;

    @Test
    public void testCreateOrder_Success() throws Exception {
        List<ApiCreateOrderDTO.ProductOrder> products = Arrays.asList(
            new ApiCreateOrderDTO.ProductOrder(2, 1),
            new ApiCreateOrderDTO.ProductOrder(3, 3)
        );
        ApiCreateOrderDTO inputOrder = new ApiCreateOrderDTO(1, 3, products);

        ApiOrderDTO mockResponse = new ApiOrderDTO();
        mockResponse.setId(1);
        mockResponse.setCustomerId(3);
        mockResponse.setRestaurantId(1);
        mockResponse.setStatus("pending");

        when(orderService.createOrder(any(ApiCreateOrderDTO.class))).thenReturn(mockResponse);

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
        ApiCreateOrderDTO invalidOrder = new ApiCreateOrderDTO(0, 0, null);
        
        when(orderService.createOrder(any(ApiCreateOrderDTO.class)))
            .thenThrow(new BadRequestException("Invalid or missing parameters"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidOrder)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));
    }

    @Test
    public void testGetOrdersByUserTypeAndId_Success() throws Exception {
        List<ApiOrderDTO> mockOrders = Arrays.asList(
            createMockOrder(3, 5, "7757 Darwin Causeway, Gerlachfort, 19822", 
                           1, "Fast Pub", "5398 Quigley Harbor, North Lynelle, 60808", 
                           3, "in progress", 5975),
            createMockOrder(13, 5, "7757 Darwin Causeway, Gerlachfort, 19822",
                           4, "Silver Grill", "5515 Sol Inlet, Shelbyfurt, 49433-4387",
                           5, "delivered", 2898)
        );

        when(orderService.getOrdersByUserTypeAndId("customer", 5)).thenReturn(mockOrders);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "customer")
                .param("id", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].customer_id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].status").value("in progress"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].total_cost").value(5975))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(13))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].status").value("delivered"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].total_cost").value(2898));
    }

    @Test
    public void testGetOrdersByUserTypeAndId_BadRequest() throws Exception {
        // Test missing type parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("id", "5"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));

        // Test missing id parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "customer"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));

        // Test invalid type parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "invalid")
                .param("id", "5"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));

        // Test invalid id parameter (non-numeric)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "customer")
                .param("id", "invalid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid or missing parameters"));
    }

    @Test
    public void testGetOrdersByUserTypeAndId_Restaurant() throws Exception {
        List<ApiOrderDTO> mockOrders = Arrays.asList(createMockOrder(1, 2, "123 Main St", 1, "Fast Pub", "456 Oak Ave", 1, "pending", 2500));
        when(orderService.getOrdersByUserTypeAndId("restaurant", 1)).thenReturn(mockOrders);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "restaurant")
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].restaurant_id").value(1));
    }

    @Test
    public void testGetOrdersByUserTypeAndId_Courier() throws Exception {
        List<ApiOrderDTO> mockOrders = Arrays.asList(createMockOrder(1, 2, "123 Main St", 1, "Fast Pub", "456 Oak Ave", 1, "in transit", 3000));
        when(orderService.getOrdersByUserTypeAndId("courier", 1)).thenReturn(mockOrders);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                .param("type", "courier")
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].courier_id").value(1));
    }

    private ApiOrderDTO createMockOrder(int id, int customerId, String customerAddress,
                                       int restaurantId, String restaurantName, String restaurantAddress,
                                       int courierId, String status, int totalCost) {
        ApiOrderDTO order = new ApiOrderDTO();
        order.setId(id);
        order.setCustomerId(customerId);   
        order.setCustomerAddress(customerAddress);
        order.setRestaurantId(restaurantId);
        order.setRestaurantName(restaurantName);
        order.setRestaurantAddress(restaurantAddress);
        order.setCourierId(courierId);
        order.setStatus(status);
        order.setTotalCost(totalCost);
        return order;
    }
    
}
