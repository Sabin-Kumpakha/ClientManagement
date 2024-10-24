package com.connect.ClientManagement.service;

import com.connect.ClientManagement.dto.ClientDto;
import com.connect.ClientManagement.exceptions.EmailAlreadyExistsException;
import com.connect.ClientManagement.exceptions.EmailAndPhoneAlreadyExistsException;
import com.connect.ClientManagement.exceptions.PhoneAlreadyExistsException;
import com.connect.ClientManagement.model.Client;
import com.connect.ClientManagement.repository.ClientRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    public final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    public List<Client> getAllClients(){
        return clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Client> getClientById(Integer id){
        return clientRepo.findById(id);
    }

    public ClientDto saveClient(ClientDto clientDto, MultipartFile image) throws IOException {
        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhoneNumber(clientDto.getPhoneNumber());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
        client.setCreatedAt(new Date());
        // Handle image data if it's not empty or null
        if (image != null && !image.isEmpty()) {
            client.setImageName(image.getOriginalFilename());
            client.setImageType(image.getContentType());
            client.setImageData(image.getBytes());
        } else {
            // Handle case where image is not provided (e.g., optional image upload)
            client.setImageName(null);
            client.setImageType(null);
            client.setImageData(null);
        }
//        clientRepo.save(client);
        Client savedClient = clientRepo.save(client);

        // Convert Entity to DTO
        ClientDto savedClientDto = new ClientDto();
        savedClientDto.setFirstName(savedClient.getFirstName());
        savedClientDto.setLastName(savedClient.getLastName());
        savedClientDto.setEmail(savedClient.getEmail());
        savedClientDto.setPhoneNumber(savedClient.getPhoneNumber());
        savedClientDto.setAddress(savedClient.getAddress());
        savedClientDto.setStatus(savedClient.getStatus());
        // Set image details in DTO (if available)
        if (savedClient.getImageName() != null) {
            savedClientDto.setImageName(savedClient.getImageName());
            savedClientDto.setImageType(savedClient.getImageType());
            savedClientDto.setImageData(savedClient.getImageData());
        } else {
            savedClientDto.setImageName(null);
            savedClientDto.setImageType(null);
            savedClientDto.setImageData(null);
        }
        return savedClientDto;

    }

    public void deleteClient(Integer id){
        Client client = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found with id: "+id));
        clientRepo.delete(client);
    }

    public boolean isEmailTaken(String email) {
        return clientRepo.findByEmail(email) != null;   // if the email is not null it means it exists
    }

    public boolean isPhoneNumberTaken(String phoneNumber) {
        return clientRepo.findByPhoneNumber(phoneNumber) != null;
    }

    public boolean isEmailAndPhoneNumberTaken(String email, String phoneNumber) {
        return clientRepo.findByEmailAndPhoneNumber(email, phoneNumber) != null;
    }

    public ClientDto updateClient(Integer id, ClientDto clientDto, BindingResult result, MultipartFile image) throws IOException {
        Client existingClient = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found with id: "+id));

        // checking if email id already exists
        if (isEmailTaken(clientDto.getEmail()) && !existingClient.getEmail().equals(clientDto.getEmail())) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address '"+clientDto.getEmail()+"' is already used"));
            throw new RuntimeException("Email address is already used");
        }
        // checking if phone Number already exists
        if (isPhoneNumberTaken(clientDto.getPhoneNumber()) && !existingClient.getPhoneNumber().equals(clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "phoneNumber", clientDto.getPhoneNumber(), false, null, null, "Phone number '"+clientDto.getPhoneNumber()+"' is already used"));
            throw new RuntimeException("Phone number is already used");
        }
        // checking if both email and Phone exists
        if (isEmailAndPhoneNumberTaken(clientDto.getEmail(), clientDto.getPhoneNumber()) &&
                (!existingClient.getEmail().equals(clientDto.getEmail()) || !existingClient.getPhoneNumber().equals(clientDto.getPhoneNumber()))) {
            result.addError(new FieldError("clientDto", "emailAndPhoneNumber", "Both email '"+clientDto.getEmail()+"' and phone number '"+clientDto.getPhoneNumber()+"' are already used"));
            throw new RuntimeException("Both email and phone number are already used");
        }

        // for validation
        existingClient.setFirstName(clientDto.getFirstName());
        existingClient.setLastName(clientDto.getLastName());
        existingClient.setEmail(clientDto.getEmail());
        existingClient.setPhoneNumber(clientDto.getPhoneNumber());
        existingClient.setAddress(clientDto.getAddress());
        existingClient.setStatus(clientDto.getStatus());
        existingClient.setUpdatedAt(new Date());
        // Handle image update only if a new image is uploaded
        if (image != null && !image.isEmpty()) {
            existingClient.setImageName(image.getOriginalFilename());
            existingClient.setImageType(image.getContentType());
            existingClient.setImageData(image.getBytes());
        }
        Client updatedClient = clientRepo.save(existingClient);

        // Convert to Dto
        ClientDto updatedClientDto = new ClientDto();
        updatedClientDto.setFirstName(updatedClient.getFirstName());
        updatedClientDto.setLastName(updatedClient.getLastName());
        updatedClientDto.setEmail(updatedClient.getEmail());
        updatedClientDto.setPhoneNumber(updatedClient.getPhoneNumber());
        updatedClientDto.setAddress(updatedClient.getAddress());
        updatedClientDto.setStatus(updatedClient.getStatus());
        // Handle image URL in DTO if the client has an image
        if (updatedClient.getImageName() != null) {
            updatedClientDto.setImageName(updatedClient.getImageName());
            updatedClientDto.setImageType(updatedClient.getImageType());
            updatedClientDto.setImageData(updatedClient.getImageData());
        } else {
            updatedClientDto.setImageName(null);
            updatedClientDto.setImageType(null);
            updatedClientDto.setImageData(null);
        }

        return updatedClientDto;
    }

}
