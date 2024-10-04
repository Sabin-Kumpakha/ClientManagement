package com.connect.ClientManagement.controller;

import com.connect.ClientManagement.model.Client;
import com.connect.ClientManagement.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public ClientService clientService;

    @GetMapping("/userPage")
    public String userPage() {
        return "userPage";
    }

    @GetMapping("/allClients")
    public String getClients(Model model) {
        var clients = clientService.getAllClients();
        model.addAttribute("clients", clients);  // Fixed attribute name (it should be plural)
        return "home";
    }

}
