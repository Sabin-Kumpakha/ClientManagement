package com.connect.ClientManagement.controller;

import com.connect.ClientManagement.dto.UserDto;

import com.connect.ClientManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userServices;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, @RequestParam(value = "success", required = false) String success) {
        model.addAttribute("userDto", new UserDto());
        // Pass success flag to the template
        if (success != null) {
            model.addAttribute("success", true);
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult result){

        if (userServices.userExistsByEmail(userDto.getEmail())) {
            result.addError(new FieldError("userDto", "email", userDto.getEmail(), false, null, null, "Email address is already used"));
        }
        if (userServices.userExistsByPhoneNumber(userDto.getPhoneNumber())) {
            result.addError(new FieldError("userDto", "phoneNumber", userDto.getPhoneNumber(), false, null, null, "Phone number is already used"));
        }
        if (userServices.userExistsByEmailAndPhoneNumber(userDto.getEmail(), userDto.getPhoneNumber())) {
            result.addError(new FieldError("userDto", "emailAndPhoneNumber", "Both email and phone number are already used"));
        }

        if (result.hasErrors()) {
            return "register";
        }

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userServices.saveUser(userDto);
        return "redirect:/register?success=true";

//        return "redirect:/login";   // Redirect to login page after successful registration
    }

}
