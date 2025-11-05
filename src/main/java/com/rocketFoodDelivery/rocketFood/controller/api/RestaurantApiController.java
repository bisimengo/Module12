package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseSuccessDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseErrorDTO;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RestaurantApiController {
    private RestaurantService restaurantService;
    private AuthService authService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService, AuthService authService) {
        this.restaurantService = restaurantService;
        this.authService = authService;
    }

    /**
     * Authenticates a user and returns an access token.
     *
     * @param authRequest The authentication data containing email and password.
     * @return ResponseEntity with the access token if successful, or error response if authentication fails.
     */
    @PostMapping("/api/auth")
    public ResponseEntity<Object> authenticate(@RequestBody @Valid AuthRequestDTO authRequest) {
        try {
            String token = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());
            
            if (token != null) {
                AuthResponseSuccessDTO response = new AuthResponseSuccessDTO(token, true);
                return ResponseBuilder.buildOkResponse(response);
            } else {
                AuthResponseErrorDTO errorResponse = new AuthResponseErrorDTO(false);
                return ResponseBuilder.buildErrorResponse(errorResponse);
            }
        } catch (Exception e) {
            AuthResponseErrorDTO errorResponse = new AuthResponseErrorDTO(false);
            return ResponseBuilder.buildErrorResponse(errorResponse);
        }
    }

    // TODO

    /**
     * Creates a new restaurant. (POST)
     *
     * @param restaurant The data for the new restaurant.
     * @return ResponseEntity with the created restaurant's data, or a BadRequestException if creation fails.
     */
    @PostMapping("/api/restaurants")
    public ResponseEntity<Object> createRestaurant(@RequestBody @Valid ApiCreateRestaurantDTO restaurant) {
        try {
            Optional<ApiCreateRestaurantDTO> createdRestaurant = restaurantService.createRestaurant(restaurant);
            
            if (createdRestaurant.isPresent()) {
                return ResponseBuilder.buildOkResponse(createdRestaurant.get());
            } else {
                throw new BadRequestException("Failed to create restaurant. User may not exist or invalid data provided.");
            }
        } catch (Exception e) {
            throw new BadRequestException("Error creating restaurant: " + e.getMessage());
        }
    }

    
    // TODO

    /**
     * Deletes a restaurant by ID.(DELETE)
     *
     * @param id The ID of the restaurant to delete.
     * @return ResponseEntity with a success message, or a ResourceNotFoundException if the restaurant is not found.
     */
    @DeleteMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
        try {
            restaurantService.deleteRestaurant(id);
            return ResponseBuilder.buildOkResponse("Restaurant deleted successfully");
        } catch (Exception e) {
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
    }

    // TODO

    /**
     * Updates an existing restaurant by ID.(PUT)
     *
     * @param id                    The ID of the restaurant to update.
     * @param restaurantUpdateData  The updated data for the restaurant.
     * @param result                BindingResult for validation.
     * @return ResponseEntity with the updated restaurant's data
     */
    @PutMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable("id") int id, @Valid @RequestBody ApiCreateRestaurantDTO restaurantUpdateData, BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException("Validation errors in request data");
        }
        
        try {
            Optional<ApiCreateRestaurantDTO> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateData);
            
            if (updatedRestaurant.isPresent()) {
                return ResponseBuilder.buildOkResponse(updatedRestaurant.get());
            } else {
                throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
            }
        } catch (Exception e) {
            throw new BadRequestException("Error updating restaurant: " + e.getMessage());
        }
    }

    /**
     * Retrieves details for a restaurant, including its average rating, based on the provided restaurant ID.(GET)
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP 404 Not Found otherwise.
     *
     * @see RestaurantService#findRestaurantWithAverageRatingById(int) for details on retrieving restaurant information.
     */
    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDTO> restaurantWithRatingOptional = restaurantService.findRestaurantWithAverageRatingById(id);
        if (!restaurantWithRatingOptional.isPresent()) throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    /**
     * Returns a list of restaurants given a rating and price range(GET).
     *
     * @param rating integer from 1 to 5 (optional)
     * @param priceRange integer from 1 to 3 (optional)
     * @return A list of restaurants that match the specified criteria
     * 
     * @see RestaurantService#findRestaurantsByRatingAndPriceRange(Integer, Integer) for details on retrieving restaurant information.
     */

    @GetMapping("/api/restaurants")
    public ResponseEntity<Object> getAllRestaurants(
        @RequestParam(name = "rating", required = false) Integer rating,
        @RequestParam(name = "price_range", required = false) Integer priceRange) {
        return ResponseBuilder.buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }

   
}
