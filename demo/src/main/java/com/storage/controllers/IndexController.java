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


    //Todo
    //  ** добавить эксепшен для поиска несуществующей директории
    // 3) разобраться с breadcrumbs(что придумал -> передаем при заходе стандартный путь в качестве параметра
    // модели, далле если переходим по директории передаем в модель шорт нейм папки или что то придумать с разделением)
    // 4) добавить ссылку при нажатии на карточку с папкой(должен обнавляться путь)
    // 5) Переименовывание файла
    // 6) Переименовывание папки
    // 7) посмотреть загрузку папок(вроде не работает загрузка пустой)

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


    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
