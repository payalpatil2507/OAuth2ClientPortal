package com.example.oauth2clientportal.Controller;

import com.example.oauth2clientportal.model.User;
import com.example.oauth2clientportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userService.findByUsername(username).get();

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/profile";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";  // Redirect back to the login page with an error message
    }

    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        // Get the username or email depending on the authentication type
        String userIdentifier = getUserIdentifier(authentication);

        if (userIdentifier != null) {
            // Fetch the user from the database using the identifier (email or username)
            User user = userService.findByUsername(userIdentifier).orElse(null);

            if (user != null) {
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getUsername());
                model.addAttribute("picture", user.getProfileImage());
                model.addAttribute("aboutUser", user.getAboutUser());
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("error", "Authentication error");
        }

        return "profile"; // Return the profile page view
    }

    // Helper method to get the user identifier (email for OAuth2, username for form login)
    private String getUserIdentifier(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
            return oauth2User.getAttribute("email");  // Get email from OAuth2User
        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return userDetails.getUsername();  // Get username from UserDetails
        }
        return null;  // Return null if authentication principal is not recognized
    }

}
