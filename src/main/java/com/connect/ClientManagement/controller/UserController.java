package com.connect.ClientManagement.controller;

import com.connect.ClientManagement.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/clients")
public class UserController {

    @Autowired
    public ClientService clientService;

    @GetMapping("/")
    public String index() {
        return "users/welcome";
    }

    @GetMapping("/allClients")
    public String getClients(Model model) {
        var clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "users/allClients";
    }

}
