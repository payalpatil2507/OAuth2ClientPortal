package com.example.oauth2clientportal.Controller;

import com.example.oauth2clientportal.dto.RegistrationDTO;
import com.example.oauth2clientportal.model.User;
import com.example.oauth2clientportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        return "register";  // Name of the Thymeleaf template (register.html)
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationDTO registrationDTO, Model model) {
        User user = userService.registerUser(registrationDTO);
        model.addAttribute("message", "Registration successful!");
        return "redirect:/login";  // Redirect to the login page after successful registration
    }
}

