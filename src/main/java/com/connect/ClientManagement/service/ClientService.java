package com.connect.ClientManagement.service;

import com.connect.ClientManagement.dto.ClientDto;
import com.connect.ClientManagement.model.Client;
import com.connect.ClientManagement.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    public ClientRepository clientRepo;

    public List<Client> getAllClients(){
        return clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Client> getClientById(Integer id){
        return clientRepo.findById(id);
    }

    public void saveClient(ClientDto clientDto){
        // for validation
        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhoneNumber(clientDto.getPhoneNumber());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
        client.setCreatedAt(new Date());
        // for validation
        clientRepo.save(client);
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

    public void updateClient(Integer id, ClientDto clientDto, BindingResult result){
        Client client = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found with id: "+id));

        // checking if email id already exists
        if (isEmailTaken(clientDto.getEmail()) && !client.getEmail().equals(clientDto.getEmail())) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address is already used"));
            throw new RuntimeException("Email address is already used");
        }
        // checking if phone Number already exists
        if (isPhoneNumberTaken(clientDto.getPhoneNumber()) && !client.getPhoneNumber().equals(clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "phoneNumber", clientDto.getPhoneNumber(), false, null, null, "Phone number is already used"));
            throw new RuntimeException("Phone number is already used");
        }
        // checking if both email and Phone exists
        if (isEmailAndPhoneNumberTaken(clientDto.getEmail(), clientDto.getPhoneNumber()) &&
                (!client.getEmail().equals(clientDto.getEmail()) || !client.getPhoneNumber().equals(clientDto.getPhoneNumber()))) {
            result.addError(new FieldError("clientDto", "emailAndPhoneNumber", "Both email and phone number are already used"));
            throw new RuntimeException("Both email and phone number are already used");
        }

        // for validation
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhoneNumber(clientDto.getPhoneNumber());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
        client.setUpdatedAt(new Date());

        clientRepo.save(client);
    }

}
