package com.storage.controllers;

import com.storage.model.User;
import com.storage.services.UserService;
import com.storage.validation.UserValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.RequestContextFilter;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserValidation userValidation;
    private final RequestContextFilter requestContextFilter;

    @Autowired
    public AuthController(UserService userService, UserValidation userValidation, RequestContextFilter requestContextFilter) {
        this.userService = userService;
        this.userValidation = userValidation;
        this.requestContextFilter = requestContextFilter;
    }

    //TODO сделать отдельные обработчики для создания пользователя и его обычной аутентификации
    //TODO посмотреть как и добавить сессии при аутентификации

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

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user, BindingResult result,HttpServletRequest request, Model model) throws ServletException {
        //Валидация наличия пользователя с таким email
        userValidation.validate(user, result);
        System.out.println("Received user: " + user.getUsername() + ", " + user.getPassword()); // Логирование

        if(result.hasErrors()) {
            return "registration";
        }
        try {
            userService.createUser(user.getUsername(), user.getPassword());
        } catch (jakarta.validation.ConstraintViolationException e) {
            result.rejectValue("username", "", "Invalid email format. Please provide a valid email.");
            return "registration";
        }
        authenticateUser(user,request);
        return "redirect:/hello";
    }

    private void authenticateUser(User user, HttpServletRequest request) throws ServletException {
        request.getSession().setAttribute("user", user);
        request.login(user.getUsername(),user.getPassword());
    }

}
