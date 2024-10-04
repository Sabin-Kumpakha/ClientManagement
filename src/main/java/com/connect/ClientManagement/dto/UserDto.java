package com.connect.ClientManagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotEmpty(message = "Username is required")
    @Size(min = 4, message = "Username must be grater than 4 digits")
    private String username;

    @NotEmpty(message = "Phone Number is required")
    private String phoneNumber;

    @NotEmpty(message = "Email id is required")
    @Email
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be greater than 6 digits")
    private String password;
    
    private String role="USER";

}
