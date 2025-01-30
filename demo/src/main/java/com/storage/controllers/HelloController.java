package com.storage.controllers;

import com.storage.model.User;
import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    private final FileService fileService;
    private final UserService userService;
    @Autowired
    public HelloController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @SneakyThrows
    @GetMapping("/hello")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        //fileService.uploadFile(user.getUser().getId(),"user-20-files/test/minioLaunch","G:\\CloudStorage\\demo\\src\\main\\resources\\minioLaunch.txt","text/plain");
        fileService.renameFile(user.getUser().getId(),"miniolaunnch123","user-20-files/test/minioLaunch");
        return "home";
    }

}
