package com.connect.ClientManagement.model;

import com.connect.ClientManagement.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;      // New, Permanent, Lead, Occasional, Inactive

    private Date createdAt;
    private Date updatedAt;

    private String imageName;
    private String imageType;
    @Lob    //Large Binary Object
    private byte[] imageData;

}
