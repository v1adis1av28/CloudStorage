package com.storage.controllers;

import com.storage.model.User;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import com.storage.validation.UserValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserValidation userValidation;

    private final FileService fileService;
    @Autowired
    public AuthController(UserService userService, UserValidation userValidation,  FileService fileService) {
        this.userService = userService;
        this.userValidation = userValidation;
        this.fileService = fileService;
    }

    @GetMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/hello";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "registration";
        }

        return "redirect:/hello";
    }

    @SneakyThrows
    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, Model model) throws ServletException {
        userValidation.validate(user, result);

        if (user.getPassword().isEmpty()) {
            result.rejectValue("password", "", "Password field should not be empty");
        }

        if (!userValidation.validateEmailField(user.getUsername())) {
            result.rejectValue("username", "", "Invalid email format. Please provide a valid email.");
        }

        if (result.hasErrors()) {
            return "registration";
        }

        try {
            userService.createUser(user.getUsername(), user.getPassword());
        } catch (jakarta.validation.ConstraintViolationException e) {
            result.rejectValue("username", "", "User with this email already exists.");
            return "registration";
        }
        authenticateUser(user, request);
        fileService.createInitialUserFolder(user.getId());

        return "redirect:/hello";
    }
  
    private void authenticateUser(User user, HttpServletRequest request) throws ServletException {
        request.getSession().setAttribute("user", user);
        request.login(user.getUsername(),user.getPassword());
    }

}
