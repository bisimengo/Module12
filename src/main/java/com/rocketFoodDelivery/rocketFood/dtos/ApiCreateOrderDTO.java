package com.rocketFoodDelivery.rocketFood.dtos;

import java.util.List;

public class ApiCreateOrderDTO {
    private int restaurantId;
    private int customerId;
    private List<ProductOrder> products;
    
    // Constructors
    public ApiCreateOrderDTO() {}
    
    public ApiCreateOrderDTO(int restaurantId, int customerId, List<ProductOrder> products) {
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.products = products;
    }
    
    // Inner class for products
    public static class ProductOrder {
        private int id;
        private int quantity;
        
        public ProductOrder() {}
        
        public ProductOrder(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
    
    // Getters and setters
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public List<ProductOrder> getProducts() { return products; }
    public void setProducts(List<ProductOrder> products) { this.products = products; }
}