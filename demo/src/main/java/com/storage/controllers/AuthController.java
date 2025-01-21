package com.storage.controllers;

import com.storage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }

    //TODO сделать отдельные обработчики для создания пользователя и его обычной аутентификации
    //TODO Разобраться с переадрессацией при входе в приложение
    //TODO посмотреть как и добавить сессии при аутентификации
    //todo добавить обработчик для logout

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginization(@RequestParam String username, @RequestParam String password, Model model) {
        return userService.createUser(username, password);
    }
}
