package com.storage.controllers;

import com.storage.model.User;
import com.storage.services.UserService;
import com.storage.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserValidation userValidation;

    @Autowired
    public AuthController(UserService userService, UserValidation userValidation) {
        this.userService = userService;
        this.userValidation = userValidation;
    }

    //TODO сделать отдельные обработчики для создания пользователя и его обычной аутентификации
    //TODO посмотреть как и добавить сессии при аутентификации

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user)
    {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("user") User user, BindingResult result) {
        //Валидация наличия пользователя с таким email
        userValidation.validate(user, result);

        if(result.hasErrors()) {
            return "registration";
        }
        return "redirect:/hello";
    }


}
