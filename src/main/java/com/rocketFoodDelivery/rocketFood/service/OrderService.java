package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductForOrderDTO;
import com.rocketFoodDelivery.rocketFood.entities.Order;
import com.rocketFoodDelivery.rocketFood.entities.User;
import com.rocketFoodDelivery.rocketFood.entities.Restaurant;
import com.rocketFoodDelivery.rocketFood.entities.Product;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
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
    
    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
                       RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new order and returns the complete order details
     * @param orderRequest The order request containing customer, restaurant, and product details
     * @return ApiOrderDTO with complete order information
     */
    @Transactional
    public ApiOrderDTO addOrder(ApiOrderRequestDTO orderRequest) {
        try {
            // 1. Validate customer exists
            Optional<User> customer = userRepository.findById(orderRequest.getCustomerId());
            if (customer.isEmpty()) {
                throw new RuntimeException("Customer not found");
            }

            // 2. Validate restaurant exists
            Optional<Restaurant> restaurant = restaurantRepository.findById(orderRequest.getRestaurantId());
            if (restaurant.isEmpty()) {
                throw new RuntimeException("Restaurant not found");
            }

            // 3. Create the order
            Order newOrder = new Order();
            newOrder.setCustomer(customer.get());
            newOrder.setRestaurant(restaurant.get());
            newOrder.setStatus("in progress"); // Default status
            
            // Assign courier if provided
            if (orderRequest.getCourierId() != null) {
                Optional<User> courier = userRepository.findById(orderRequest.getCourierId());
                courier.ifPresent(newOrder::setCourier);
            }

            // 4. Save the order
            Order savedOrder = orderRepository.save(newOrder);

            // 5. Process products and calculate costs
            List<ApiProductForOrderDTO> productDTOs = orderRequest.getProducts().stream()
                .map(productRequest -> {
                    Optional<Product> product = productRepository.findById(productRequest.getProductId());
                    if (product.isPresent()) {
                        Product p = product.get();
                        int totalCost = p.getCost() * productRequest.getQuantity();
                        
                        return new ApiProductForOrderDTO(
                            p.getId(),
                            p.getName(),
                            productRequest.getQuantity(),
                            p.getCost(),
                            totalCost
                        );
                    }
                    throw new RuntimeException("Product not found: " + productRequest.getProductId());
                })
                .collect(Collectors.toList());

            // 6. Build and return the response DTO
            return new ApiOrderDTO(
                savedOrder.getId(),
                customer.get().getId(),
                customer.get().getName(),
                customer.get().getAddress() != null ? customer.get().getAddress().getFullAddress() : "",
                restaurant.get().getId(),
                restaurant.get().getName(),
                restaurant.get().getAddress() != null ? restaurant.get().getAddress().getFullAddress() : "",
                savedOrder.getCourier() != null ? savedOrder.getCourier().getId() : null,
                savedOrder.getCourier() != null ? savedOrder.getCourier().getName() : null,
                savedOrder.getStatus(),
                productDTOs
            );

        } catch (Exception e) {
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new order (wrapper for existing addOrder method)
     */
    @Transactional
    public ApiOrderDTO createOrder(ApiOrderRequestDTO orderRequest) {
        return addOrder(orderRequest); // Use your existing addOrder method
    }

    /**
     * Updates order status
     */
    @Transactional
    public ApiOrderStatusDTO updateOrderStatus(int orderId, ApiOrderStatusDTO statusDTO) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }
        
        Order order = orderOpt.get();
        order.setStatus(statusDTO.getStatus());
        orderRepository.save(order);
        
        return new ApiOrderStatusDTO(order.getId(), order.getStatus());
    }

    /**
     * Gets orders by user type and ID
     */
    public List<ApiOrderDTO> getOrdersByUserTypeAndId(String userType, int userId) {
        List<Order> orders;
        
        switch (userType.toLowerCase()) {
            case "customer":
                orders = orderRepository.findByCustomerId(userId);
                break;
            case "restaurant":
                orders = orderRepository.findByRestaurantId(userId);
                break;
            case "courier":
                orders = orderRepository.findByCourierId(userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
        
        return orders.stream()
            .map(this::convertToApiOrderDTO)
            .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Order entity to ApiOrderDTO
     */
    private ApiOrderDTO convertToApiOrderDTO(Order order) {
        return new ApiOrderDTO(
            order.getId(),
            order.getCustomer().getId(),
            order.getCustomer().getName(),
            order.getCustomer().getAddress() != null ? order.getCustomer().getAddress().getFullAddress() : "",
            order.getRestaurant().getId(),
            order.getRestaurant().getName(),
            order.getRestaurant().getAddress() != null ? order.getRestaurant().getAddress().getFullAddress() : "",
            order.getCourier() != null ? order.getCourier().getId() : null,
            order.getCourier() != null ? order.getCourier().getName() : null,
            order.getStatus(),
            List.of() // You'll need to add product details here
        );
    }
 

}