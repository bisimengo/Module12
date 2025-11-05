package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user with email and password
     *
     * @param email The user's email
     * @param password The user's password
     * @return Access token if authentication successful, null otherwise
     */
    public String authenticate(String email, String password) {
        try {
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return null;
            }
            
            UserEntity user = userOptional.get();
            
            // Simple password check (in production, use proper password hashing)
            if (!user.getPassword().equals(password)) {
                return null;
            }
            
            // Generate access token
            return generateAccessToken(user);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Generates an access token for the authenticated user
     */
    private String generateAccessToken(UserEntity user) {
        return "access_token_" + user.getId() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}