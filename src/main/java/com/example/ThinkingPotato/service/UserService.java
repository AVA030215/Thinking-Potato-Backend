package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.repository.UserRepository;
import com.example.ThinkingPotato.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email); // Return null if not found
    }


    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }


}
