package com.storage.controllers;

import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
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

    //Plan:
    //1) Параметризировать контроллер задавая корневую папку клиента
    //2) Передавать этот параметр в файлсервис, от туда получать файлы/папки из параметра
    //3) Передаем на фронт данные(пока только отображаем название файла\папки в виде карточки)
    //4) Передаем текущую директорию для отображения тоже в виде ссылки
    //
    //

    @SneakyThrows
    @GetMapping(value = "/hello")
    public String index(@RequestParam(value = "path", required = false) String path,Model model) {
        model.addAttribute("User", getCurrentUser().getUser());
        if(path != null) {
            model.addAttribute("path", path);
            model.addAttribute("files", fileService.getFolderObjects(path));
        }
        else
        {
            model.addAttribute("files",fileService.getFolderObjects(String.format(userRootFolder, getCurrentUser().getUser().getId())));
        }
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
