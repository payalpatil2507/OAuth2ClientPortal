package com.example.oauth2clientportal.dto;

import lombok.Data;

@Data
public class RegistrationDTO {
    private String username;
    private String password;
    private String name;
    private String aboutUser;
    private String profile;
}
