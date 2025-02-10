package com.storage.controllers;

import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import com.storage.utils.BreadcrumbsHandle;
import io.minio.MinioClient;
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

    private final FileService fileService;
    private final UserService userService;
    private final String userRootFolder = "user-%d-files/";

    @Autowired
    public IndexController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }
    //Todo
    // Выкатить в мейн ветку мвп
    // Подумать над изменением формата файлов, например сделать регулярное выражение чтобы не менялось расширение
    // Реализовать Поиск файла, страница, форма поиска, и сервис поиска Контент
    // Форма поиска файлов и папок по названию
    // Список найденных файлов. Для каждого найденного файла отображаем имя и кнопку для перехода в папку, содержащую данный файл
    // 3) Реализовать скачивание файлов
    // 4) Добавить спринг сесси и редис
    // 5) Рефакторинг кода + чистка
    // 6) Докер компоуз
    @SneakyThrows
    @GetMapping(value = "/hello")
    public String index(@RequestParam(value = "path", required = false) String path, Model model) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }

        String currentPath = (path == null || path.isEmpty()) ? "" : path;

        if (path != null) {
            model.addAttribute("chain", BreadcrumbsHandle.createBreadcrumbsByPath(path));
            model.addAttribute("path", path);
            model.addAttribute("files", fileService.getFolderObjects(path));
        } else {
            String userRoot = String.format(userRootFolder, getCurrentUser().getUser().getId());
            model.addAttribute("chain", BreadcrumbsHandle.createBreadcrumbsByPath(userRoot));
            model.addAttribute("files", fileService.getFolderObjects(userRoot));
        }

        model.addAttribute("currentPath", currentPath);
        model.addAttribute("User", getCurrentUser().getUser());
        return "home";
    }

    //Требуется перенос в userservice
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }

}
