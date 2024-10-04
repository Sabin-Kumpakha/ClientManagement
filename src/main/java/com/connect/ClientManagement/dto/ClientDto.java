package com.connect.ClientManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    @NotEmpty(message = "The First Name is required")
    private String firstName;

    @NotEmpty(message = "The Last Name is required")
    private String lastName;

    @NotEmpty(message = "The Email is required")
    @Email
    private String email;

    @NotEmpty(message = "The Phone Number is required")
    private String phoneNumber;

    private String address;

    @NotEmpty(message = "The Status is required")
    private String status;      // New, Permanent, Lead, Occasional, Inactive

}