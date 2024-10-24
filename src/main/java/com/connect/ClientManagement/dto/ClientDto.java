package com.connect.ClientManagement.dto;

import com.connect.ClientManagement.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    @NotEmpty(message = "The First Name is required")
    private String firstName;

    @NotEmpty(message = "The Last Name is required")
    private String lastName;

    @NotEmpty(message = "The Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotEmpty(message = "The Phone Number is required")
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String imageName;
    private String imageType;
    @Lob    //Large Binary Object
    private byte[] imageData;

}