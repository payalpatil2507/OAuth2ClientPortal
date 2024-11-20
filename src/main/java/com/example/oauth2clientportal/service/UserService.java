package com.example.oauth2clientportal.service;

import com.example.oauth2clientportal.dto.RegistrationDTO;
import com.example.oauth2clientportal.model.User;
import com.example.oauth2clientportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegistrationDTO registrationDTO) {
        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(registrationDTO.getPassword());

        // Create a new User object
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(encryptedPassword);
        user.setName(registrationDTO.getName());
        user.setAboutUser(registrationDTO.getAboutUser());

        // Save the user to the database
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
