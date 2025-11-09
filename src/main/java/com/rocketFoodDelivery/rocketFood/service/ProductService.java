package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.google.common.collect.Collectors;

@Service
public class ProductService {
    
    private ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Finds all products for a specific restaurant
     * @param restaurantId The ID of the restaurant
     * @return List of ApiProductDTO for the restaurant
     */
    public List<ApiProductDTO> findProductsByRestaurantId(int restaurantId) {
        List<Product> products = productRepository.findByRestaurantId(restaurantId);
        return products.stream()
            .map(this::convertToApiDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Adds a new product to a restaurant
     * @param name Product name
     * @param description Product description
     * @param cost Product cost
     * @param restaurantId Restaurant ID
     */
    public void addProduct(String name, String description, double cost, int restaurantId) {
        productRepository.addProduct(name, description, cost, restaurantId);
    }
    
    /**
     * Deletes all products for a specific restaurant
     * @param restaurantId The ID of the restaurant
     */
    public void deleteProductsByRestaurantId(int restaurantId) {
        productRepository.deleteProductsByRestaurantId(restaurantId);
    }
    
    /**
     * Finds a product by ID
     * @param id Product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }
    
    /**
     * Finds all products
     * @return List of all products
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    private ApiProductDTO convertToApiDTO(Product product) {
        ApiProductDTO dto = new ApiProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCost(product.getCost());
        return dto;
    }
}