package com.connect.ClientManagement.controller;

import com.connect.ClientManagement.dto.ClientDto;
import com.connect.ClientManagement.model.Client;
import com.connect.ClientManagement.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/clients")
public class ClientController {

    @Autowired
    public ClientService clientService;

    @GetMapping("/")
    public String index() {
        return "admin/welcome";
    }

    @GetMapping("/allClients")
    public String getClients(Model model) {
        var clients = clientService.getAllClients();
        model.addAttribute("clients", clients);  // Fixed attribute name (it should be plural)
        return "admin/allClients";    // templates --> clients --> allClients
    }

    @GetMapping("/create")
    public String createClient(Model model) {
        model.addAttribute("clientDto", new ClientDto());  // Changed to ClientDto
        return "admin/create";
    }

    @PostMapping("/create")
    public String createClient(@Valid @ModelAttribute ClientDto clientDto, BindingResult result) {
        if (clientService.isEmailTaken(clientDto.getEmail())) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address is already used"));
        }
        if (clientService.isPhoneNumberTaken(clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "phoneNumber", clientDto.getPhoneNumber(), false, null, null, "Phone number is already used"));
        }
        if (clientService.isEmailAndPhoneNumberTaken(clientDto.getEmail(), clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "emailAndPhoneNumber", "Both email and phone number are already used"));
        }

        if (result.hasErrors()) {
            return "admin/create";
        }

        clientService.saveClient(clientDto);
        return "redirect:/admin/clients/allClients";    // redirect to all clients page after creating new client
    }

    @GetMapping("/edit")
    public String editClient( @RequestParam Integer id, Model model) {
        Optional<Client> clientOptional = clientService.getClientById(id);
        if (clientOptional.isEmpty()) {
            return "redirect:/admin/clients/allClients";
        }
            Client client = clientOptional.get();
            ClientDto clientDto = new ClientDto();
            clientDto.setFirstName(client.getFirstName());
            clientDto.setLastName(client.getLastName());
            clientDto.setEmail(client.getEmail());
            clientDto.setPhoneNumber(client.getPhoneNumber());
            clientDto.setAddress(client.getAddress());
            clientDto.setStatus(client.getStatus());

            model.addAttribute("client", client);
            model.addAttribute("clientDto", clientDto);

            return "admin/edit";
    }


    @PostMapping("/edit")
    public String editClient( @Valid @ModelAttribute ClientDto clientDto, @RequestParam Integer id, BindingResult result) {
        Optional<Client> clientOptional = clientService.getClientById(id);
        if (clientOptional.isEmpty()) {
            return "redirect:/admin/clients/allClients";
        }
        if (result.hasErrors()) {
            return "admin/edit";
        }
        try {
            clientService.updateClient(id, clientDto, result);
        } catch (RuntimeException ex) {
            return "admin/edit";
        }
        return "redirect:/admin/clients/allClients";
    }

    @GetMapping("/delete")
    public String deleteClient(@RequestParam Integer id) {
        clientService.deleteClient(id);
        return "redirect:/admin/clients/allClients";
    }

}
