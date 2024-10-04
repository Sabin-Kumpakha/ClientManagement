package com.connect.ClientManagement.service;

import com.connect.ClientManagement.dto.UserDto;
import com.connect.ClientManagement.model.User;
import com.connect.ClientManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public boolean saveUser(UserDto userDto){
        // for validation
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // Set the role, use default "USER" if not provided
        if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
            user.setRole("USER");  // Default role
        } else {
            user.setRole(userDto.getRole());
        }
        // Save user and return status
        try {
            userRepo.save(user);
            return true; // Indicate success
        } catch (Exception e) {
            // Handle exceptions (log error, etc.)
            return false; // Indicate failure
        }
    }

    // Returns User if found by email
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // Returns true if a user exists by email
    public boolean userExistsByEmail(String email) {
        return findUserByEmail(email) != null;
    }

    // Returns User if found by phone number
    public User findByPhoneNumber(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber);
    }

    // Returns true if a user exists by phone number
    public boolean userExistsByPhoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber) != null;
    }

    // Returns User if found by both email and phone number
    public User findByEmailAndPhoneNumber(String email, String phoneNumber) {
        return userRepo.findByEmailAndPhoneNumber(email, phoneNumber);
    }

    // Returns true if a user exists by both email and phone number
    public boolean userExistsByEmailAndPhoneNumber(String email, String phoneNumber) {
        return findByEmailAndPhoneNumber(email, phoneNumber) != null;
    }

}
