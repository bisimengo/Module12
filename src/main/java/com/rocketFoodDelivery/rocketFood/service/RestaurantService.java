package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;


@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    @Autowired
    public RestaurantService(
        RestaurantRepository restaurantRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        ProductOrderRepository productOrderRepository,
        UserRepository userRepository,
        AddressService addressService
        ) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Retrieves (GET) a restaurant with its details, including the average rating, based on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return An Optional containing a RestaurantDTO with details such as id, name, price range, and average rating.
     *         If the restaurant with the given id is not found, an empty Optional is returned.
     *
     * @see RestaurantRepository#findRestaurantWithAverageRatingById(int) for the raw query details from the repository.
     */
    public Optional<ApiRestaurantDTO> findRestaurantWithAverageRatingById(int id) {
        List<Object[]> restaurant = restaurantRepository.findRestaurantWithAverageRatingById(id);

        if (!restaurant.isEmpty()) {
            Object[] row = restaurant.get(0);
            int restaurantId = (int) row[0];
            String name = (String) row[1];
            int priceRange = (int) row[2];
            double rating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
            int roundedRating = (int) Math.ceil(rating);
            ApiRestaurantDTO restaurantDTO = new ApiRestaurantDTO(restaurantId, name, priceRange, roundedRating);
            return Optional.of(restaurantDTO);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds (GET) restaurants based on the provided rating and price range.
     *
     * @param rating     The rating for filtering the restaurants.
     * @param priceRange The price range for filtering the restaurants.
     * @return A list of ApiRestaurantDTO objects representing the selected restaurants.
     *         Each object contains the restaurant's ID, name, price range, and a rounded-up average rating.
     */
    public List<ApiRestaurantDTO> findRestaurantsByRatingAndPriceRange(Integer rating, Integer priceRange) {
        List<Object[]> restaurants = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);

        List<ApiRestaurantDTO> restaurantDtos = new ArrayList<>();

            for (Object[] row : restaurants) {
                int restaurantId = (int) row[0];
                String name = (String) row[1];
                int range = (int) row[2];
                double avgRating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
                int roundedAvgRating = (int) Math.ceil(avgRating);
                restaurantDtos.add(new ApiRestaurantDTO(restaurantId, name, range, roundedAvgRating));
            }

            return restaurantDtos;
    }

    // TODO

    /**
     * Creates(POST) a new restaurant and returns its information.
     *
     * @param restaurant The data for the new restaurant.
     * @return An Optional containing the created restaurant's information as an ApiCreateRestaurantDto,
     *         or Optional.empty() if the user with the provided user ID does not exist or if an error occurs during creation.
     */
    @Transactional
    public Optional<ApiCreateRestaurantDTO> createRestaurant(ApiCreateRestaurantDTO restaurant) {
        try {
            // 1. Validate that the user exists
            if (!userRepository.existsById(restaurant.getUserId())) {
                return Optional.empty();
            }

            // 2. Create the address using AddressService
            Long addressId = addressService.createAddress(restaurant.getAddress());
            if (addressId == null) {
                return Optional.empty();
            }

            // 3. Save the restaurant using the repository
            restaurantRepository.saveRestaurant(
                (long) restaurant.getUserId(),  // Cast if getUserId() returns int
                addressId,
                restaurant.getName(),
                restaurant.getPriceRange(),
                restaurant.getPhone(),
                restaurant.getEmail()
            );

            // 4. Get the last inserted ID
            int newRestaurantId = restaurantRepository.getLastInsertedId();

            // 5. Return the created restaurant data
            return Optional.of(new ApiCreateRestaurantDTO(
                newRestaurantId,
                restaurant.getUserId(),
                restaurant.getAddress(),
                restaurant.getName(),
                restaurant.getPriceRange(),
                restaurant.getPhone(),
                restaurant.getEmail()
            ));

        } catch (Exception e) {
            // Log the specific error for debugging
            System.err.println("Error creating restaurant: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // TODO

    /**
     * Finds (GET) a restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing the restaurant with the specified ID,
     *         or Optional.empty() if no restaurant is found.
     */
    public Optional<Restaurant> findById(int id) {
        return restaurantRepository.findRestaurantById(id);
    }

    // TODO

    /**
     * Updates (PUT) an existing restaurant by ID with the provided data.
     *
     * @param id                  The ID of the restaurant to update.
     * @param updatedRestaurantDTO The updated data for the restaurant.
     * @return An Optional containing the updated restaurant's information as an ApiCreateRestaurantDTO,
     *         or Optional.empty() if the restaurant with the specified ID is not found or if an error occurs during the update.
     */
    @Transactional
    public Optional<ApiCreateRestaurantDTO> updateRestaurant(int id, ApiCreateRestaurantDTO updatedRestaurantDTO) {
        try {
            // 1. Check if restaurant exists
            Optional<Restaurant> existingRestaurant = restaurantRepository.findRestaurantById(id);
            if (existingRestaurant.isEmpty()) {
                return Optional.empty();
            }

            // 2. Update the restaurant using the repository
            restaurantRepository.updateRestaurant(
                id,
                updatedRestaurantDTO.getName(),
                updatedRestaurantDTO.getPriceRange(),
                updatedRestaurantDTO.getPhone()
            );

            // 3. Retrieve the updated restaurant
            Optional<Restaurant> updatedRestaurant = restaurantRepository.findRestaurantById(id);
            if (updatedRestaurant.isPresent()) {
                Restaurant restaurant = updatedRestaurant.get();
                
                // 4. Return the updated restaurant data as DTO
                return Optional.of(new ApiCreateRestaurantDTO(
                    restaurant.getId(),
                    restaurant.getUserEntity().getId(),
                    null, // Address is not updated in PUT operation
                    restaurant.getName(),
                    restaurant.getPriceRange(),
                    restaurant.getPhone(),
                    restaurant.getEmail()
                ));
            }

            return Optional.empty();

        } catch (Exception e) {
            System.err.println("Error updating restaurant: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // TODO

    /**
     * Deletes (DELETE) a restaurant along with its associated data, including its product orders, orders and products.
     *
     * @param restaurantId The ID of the restaurant to delete.
     */
    @Transactional
    public void deleteRestaurant(int restaurantId) {
        try {
            // 1. Check if restaurant exists
            Optional<Restaurant> restaurant = restaurantRepository.findRestaurantById(restaurantId);
            if (restaurant.isEmpty()) {
                throw new RuntimeException("Restaurant with ID " + restaurantId + " not found");
            }

            // 2. Get all orders for this restaurant
            List<Order> orders = orderRepository.findOrdersByRestaurantId(restaurantId);
            
            // 3. Delete product orders for each order, then delete the order
            for (Order order : orders) {
                productOrderRepository.deleteProductOrdersByOrderId(order.getId());
                orderRepository.deleteOrderById(order.getId());
            }

            // 4. Delete products associated with the restaurant
            productRepository.deleteProductsByRestaurantId(restaurantId);

            // 5. Finally, delete the restaurant
            restaurantRepository.deleteRestaurantById(restaurantId);

        } catch (Exception e) {
            System.err.println("Error deleting restaurant: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete restaurant with ID " + restaurantId);
        }
    }
}