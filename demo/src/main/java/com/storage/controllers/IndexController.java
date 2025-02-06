package com.storage.controllers;

import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @SneakyThrows
    @GetMapping("/hello")
    public String index(Model model) {
        //fileService.renameFile(user.getUser().getId(),"miniolaunnch123","user-20-files/test/minioLaunch");
        //fileService.removeFile("user-files/user-20-files/test/");
        //fileService.renameFolder("user-20-files/test/","newfoldername");
        //fileService.uploadFileTest(user.getUser().getId(),"user-20-files/test/minioLaunch","G:\\CloudStorage\\demo\\src\\main\\resources\\minioLaunch.txt","text/plain");
        //fileService.removeFolder("user-20-files/test/test/");
        model.addAttribute("User", getCurrentUser().getUser());
        return "home";
    }

    //TODO Адрес - /?path=$path_to_subdirectory. Параметр $path задаёт путь просматриваемой папки. Если параметр отсутствует, подразумевается корневая папка. Пример - /path=Projects%2FJava%2FCloudFileStorage (параметр закодирован через URL Encode)
    //TODO добавить отображение в блок файлов всех файлов пользователя по его id
    //TODO добавить в карточку(предположительно так будет отображатсья файл\папка) кнопку удаления/переименовывания(создание контроллера отвечающего за изменение файлов)


    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
