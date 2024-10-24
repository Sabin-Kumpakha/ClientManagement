package com.connect.ClientManagement.controller;

import com.connect.ClientManagement.dto.ClientDto;
import com.connect.ClientManagement.model.Client;
import com.connect.ClientManagement.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/clients")
public class ClientController {

    public final ClientService clientService;
    private static final String IMAGE_DIRECTORY = "static/img";

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String index() {
        return "admin/welcome";
    }

    @GetMapping("/image/{clientId}")
    @ResponseBody
    public ResponseEntity<byte[]> getImageByClientId(@PathVariable int clientId) {
        Optional<Client> clientOptional = clientService.getClientById(clientId);

        if (clientOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if client not found
        }

        Client client = clientOptional.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(client.getImageType()));
        headers.setContentLength(client.getImageData().length);
        headers.set("Content-Disposition", "attachment; filename=\"" + client.getImageName() + "\"");

        return new ResponseEntity<>(client.getImageData(), headers, HttpStatus.OK);
    }

    @GetMapping("/allClients")
    public String getClients(Model model) {
        var clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "admin/allClients";
    }

    // View client details by id
    @GetMapping("/view/{id}")
    public String viewClientById(@PathVariable("id") int id, Model model) {
        Optional<Client> clientOptional = clientService.getClientById(id);
        if (clientOptional.isPresent()) {
            Client clientUser = clientOptional.get();
            model.addAttribute("client", clientUser);
        } else {
            return "redirect:/admin/clients/allClients";
        }
        return "admin/client";
    }

    @GetMapping("/create")
    public String createClient(Model model) {
        model.addAttribute("clientDto", new ClientDto());
        return "admin/create";
    }

    @PostMapping("/create")
    public String createClient(@Valid @ModelAttribute ClientDto clientDto, MultipartFile image, BindingResult result) throws IOException {
        if (clientService.isEmailTaken(clientDto.getEmail())) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address '"+clientDto.getEmail()+"' is already used"));
        }
        if (clientService.isPhoneNumberTaken(clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "phoneNumber", clientDto.getPhoneNumber(), false, null, null, "Phone number '"+clientDto.getPhoneNumber()+"' is already used"));
        }
        if (clientService.isEmailAndPhoneNumberTaken(clientDto.getEmail(), clientDto.getPhoneNumber())) {
            result.addError(new FieldError("clientDto", "emailAndPhoneNumber", "Both email and phone number '"+clientDto.getPhoneNumber()+"' are already used"));
        }
        if (result.hasErrors()) {
            return "admin/create";
        }
        // Check if the image file is not empty
        if (!image.isEmpty()) {
            clientDto.setImageName(image.getOriginalFilename());    // Set the image file name
            clientDto.setImageType(image.getContentType());         // Set the image MIME type
            clientDto.setImageData(image.getBytes());               // Convert and store the image as a byte array
        }
        try {
            clientService.saveClient(clientDto, image);  // Pass image for saving
            return "redirect:/admin/clients/allClients";
        } catch (Exception e) {
            return "redirect:admin/create";    // redirect to create page after creating new client
        }
    }

    @PostMapping("/edit/{id}")
    public String editClient(@Valid @ModelAttribute ClientDto clientDto, @PathVariable Integer id, MultipartFile image, BindingResult result, Model model,  RedirectAttributes redirectAttributes) throws IOException {
        Optional<Client> clientOptional = clientService.getClientById(id);
        if (clientOptional.isEmpty()) {
            return "redirect:/admin/clients/allClients";
        }
        if (result.hasErrors()) {
            model.addAttribute("clientDto", clientDto);
            return "admin/edit";
        }
        try {
            clientService.updateClient(id, clientDto, result, image);
            redirectAttributes.addFlashAttribute("successMsg", "Client Updated Successfully");
            return "redirect:/admin/clients/edit/{id}";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            model.addAttribute("clientDto", clientDto);
            return "admin/edit";
//            return "redirect:/admin/clients/allClients";
        }
    }


    @GetMapping("/edit/{id}")
    public String editClient( @PathVariable Integer id, Model model) {
        Optional<Client> clientOptional = clientService.getClientById(id);
        if (clientOptional.isEmpty()) {
            return "redirect:/admin/clients/allClients";
        }
            Client updatedClient = clientOptional.get();

            // Set up ClientDto with client data
            ClientDto clientDto = new ClientDto();
            clientDto.setFirstName(updatedClient.getFirstName());
            clientDto.setLastName(updatedClient.getLastName());
            clientDto.setEmail(updatedClient.getEmail());
            clientDto.setPhoneNumber(updatedClient.getPhoneNumber());
            clientDto.setAddress(updatedClient.getAddress());
            clientDto.setStatus(updatedClient.getStatus());

        // Set image details in DTO (if available)
        if (updatedClient.getImageName() != null) {
            clientDto.setImageName(updatedClient.getImageName());
            clientDto.setImageType(updatedClient.getImageType());
            clientDto.setImageData(updatedClient.getImageData());
        } else {
            clientDto.setImageName(null);
            clientDto.setImageType(null);
            clientDto.setImageData(null);
        }

            model.addAttribute("updatedClient", updatedClient);
            model.addAttribute("clientDto", clientDto);

            return "admin/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Integer id) {
        clientService.deleteClient(id);
        return "redirect:/admin/clients/allClients";
    }

}

