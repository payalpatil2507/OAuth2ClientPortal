package com.example.oauth2clientportal;

import com.example.oauth2clientportal.model.User;
import com.example.oauth2clientportal.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;


    private static final Logger logger = LogManager.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        logger.info("User Principal Name: {}", defaultOAuth2User.getName());
        defaultOAuth2User.getAttributes().forEach((key, value) -> logger.info("{} => {}", key, value));
        logger.info("User Authorities: {}", defaultOAuth2User.getAuthorities());

        String email = defaultOAuth2User.getAttribute("email");
        String name = defaultOAuth2User.getAttribute("name");
        String profileImage = defaultOAuth2User.getAttribute("picture").toString();

        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByUsername(email);

        if (existingUser.isEmpty()) {
            // Save new user if not found
            User user = new User();
            user.setUsername(email);
            user.setName(name);
            user.setProfileImage(profileImage);
            user.setPassword(UUID.randomUUID().toString());
            user.setAboutUser("Login using oAuth");
            userRepository.save(user);

            logger.info("New user created: {}", email);
        } else {
            logger.info("User already exists: {}", email);
        }

        // Redirect the user to the profile page
        new DefaultRedirectStrategy().sendRedirect(request, response, "/profile");
    }
}
