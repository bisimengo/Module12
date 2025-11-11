package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateOrderDTO; 
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductForOrderApiDTO;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.models.OrderStatus;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;          
import com.rocketFoodDelivery.rocketFood.models.Restaurant;    
import com.rocketFoodDelivery.rocketFood.models.Product;       
import com.rocketFoodDelivery.rocketFood.models.Customer;      
import com.rocketFoodDelivery.rocketFood.models.Courier;       
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import com.rocketFoodDelivery.rocketFood.repository.OrderStatusRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductOrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.CustomerRepository;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.exception.InvalidStatusTransitionException;
import com.rocketFoodDelivery.rocketFood.exception.InsufficientInventoryException; 

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @PersistenceContext
    private EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ProductOrderRepository productOrderRepository;
    private final CustomerRepository customerRepository;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                       RestaurantRepository restaurantRepository, ProductRepository productRepository,
                       OrderStatusRepository orderStatusRepository, ProductOrderRepository productOrderRepository,
                       CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.productOrderRepository = productOrderRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Creates a new order and returns the complete order details
     * @param orderRequest The order request containing customer, restaurant, and product details
     * @return ApiOrderDTO with complete order information
     */
    /*
  

    /**
     * Creates a new order using ApiCreateOrderDTO
     */
    @Transactional
    public ApiOrderDTO createOrder(ApiCreateOrderDTO createOrderDTO) {
        try {
            // 1. Validate customer exists
            if (!customerRepository.existsById(createOrderDTO.getCustomerId())) {
                throw new ResourceNotFoundException("Customer with id " + createOrderDTO.getCustomerId() + " not found");
            }

            // 2. Validate restaurant exists
            if (!restaurantRepository.existsById(createOrderDTO.getRestaurantId())) {
                throw new ResourceNotFoundException("Restaurant with id " + createOrderDTO.getRestaurantId() + " not found");
            }

            // 3. Validate products exist
            for (ApiCreateOrderDTO.ProductOrder productOrder : createOrderDTO.getProducts()) {
                Optional<Product> product = productRepository.findById(productOrder.getId());
                if (product.isEmpty()) {
                    throw new ResourceNotFoundException("Product with id " + productOrder.getId() + " not found");
                }
            }

            // 4. Create the order using repository methods
            orderRepository.createOrder(
                createOrderDTO.getCustomerId(),
                createOrderDTO.getRestaurantId(),
                1 // Default status: pending
            );
            
            // 5. Get the created order ID
            int newOrderId = orderRepository.getLastInsertedId();

            // 6. Add products to the order
            for (ApiCreateOrderDTO.ProductOrder productOrder : createOrderDTO.getProducts()) {
                productOrderRepository.createProductOrder(
                    newOrderId,
                    productOrder.getId(),
                    productOrder.getQuantity()
                );
            }

            // 7. Get the complete order details and return as ApiOrderDTO
            List<Object[]> orderData = orderRepository.findOrdersWithDetailsByOrderId(newOrderId);
            if (orderData.isEmpty()) {
                throw new RuntimeException("Failed to retrieve created order");
            }

            return convertToApiOrderDTO(orderData.get(0));

        } catch (ResourceNotFoundException | InsufficientInventoryException e) {
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    /**
     * Updates order status and returns the new status
     */
    @Transactional
    public ApiOrderStatusDTO updateOrderStatus(int orderId, ApiOrderStatusDTO statusDTO) {
        // 1. Check if order exists
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }
        
        Order order = orderOpt.get();
        
        // 2. Validate status transition (optional - add business logic if needed)
        String newStatus = statusDTO.getStatus().toLowerCase();
        if (!isValidStatusTransition(order.getOrder_status().getName(), newStatus)) {
            throw new InvalidStatusTransitionException("Cannot transition from " + 
                order.getOrder_status().getName() + " to " + newStatus);
        }
        
        // 3. Find the status entity
        Optional<OrderStatus> newStatusEntity = orderStatusRepository.findByName(newStatus);
        if (newStatusEntity.isEmpty()) {
            throw new InvalidStatusTransitionException("Invalid status: " + newStatus);
        }
        
        // 4. Update the order
        order.setOrder_status(newStatusEntity.get());
        orderRepository.save(order);
        
        // 5. Return just the status (not wrapped in message/data)
        return new ApiOrderStatusDTO(newStatus);
    }
    
    /**
     * Validates if status transition is allowed
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
    // Current logic might be rejecting valid transitions
    switch (currentStatus.toLowerCase()) {
        case "pending":
            return newStatus.equals("in_progress") || newStatus.equals("cancelled");
        case "in_progress":
            return newStatus.equals("delivered") || newStatus.equals("cancelled");
        case "delivered":
        case "cancelled":
            return false; // Cannot change from final states
        default:
            return true; // Allow any transition for unknown states
    }
    }
    
    /**
     * Gets orders by user type and ID with complete details
     */
    public List<ApiOrderDTO> getOrdersByUserTypeAndId(String userType, int userId) {
        List<Object[]> orderData;
        
        switch (userType.toLowerCase()) {
            case "customer":
                orderData = orderRepository.findOrdersWithDetailsByCustomerId(userId);
                break;
            case "restaurant":
                orderData = orderRepository.findOrdersWithDetailsByRestaurantId(userId);
                break;
            case "courier":
                orderData = orderRepository.findOrdersWithDetailsByCourierId(userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
        
        return orderData.stream()
            .map(this::convertToApiOrderDTO)
            .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Object[] result to ApiOrderDTO
     */
    private ApiOrderDTO convertToApiOrderDTO(Object[] row) {
        int orderId = ((Number) row[0]).intValue();
        int customerId = ((Number) row[1]).intValue();
        int restaurantId = ((Number) row[2]).intValue();
        Integer courierId = row[3] != null ? ((Number) row[3]).intValue() : null;
        String customerAddress = (String) row[4];
        String restaurantName = (String) row[5];
        String restaurantAddress = (String) row[6];
        String status = (String) row[7];
        
        // Get products for this order
        List<Object[]> productData = productOrderRepository.findProductsByOrderId(orderId);
        List<ApiProductForOrderApiDTO> products = productData.stream()
            .map(productRow -> new ApiProductForOrderApiDTO(
                ((Number) productRow[0]).intValue(), // product_id
                ((Number) productRow[2]).intValue(), // product_quantity  
                ((Number) productRow[3]).intValue(), // unit_cost
                ((Number) productRow[4]).intValue()  // total_cost
            ))
            .collect(Collectors.toList());
        
        // Calculate total cost
        long totalCost = products.stream()
            .mapToLong(ApiProductForOrderApiDTO::getTotalCost)
            .sum();
        
        return new ApiOrderDTO(
            orderId, customerId, customerAddress,
            restaurantId, restaurantName, restaurantAddress,
            courierId, status, products, totalCost
        );
    }
    

}