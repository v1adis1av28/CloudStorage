package com.storage.controllers;

import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import com.storage.utils.BreadcrumbsHandle;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    // 5) Рефакторинг кода + чистка
    // 6) Докер компоуз

    @SneakyThrows
    @GetMapping(value = "/hello")
    public String index(@RequestParam(value = "path", required = false) String path, Model model) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }

        String currentPath = (path == null || path.isEmpty()) ? "" : path;
        String userRoot = String.format(userRootFolder, userService.getCurrentUserId());
        if (path != null) {
            model.addAttribute("chain", BreadcrumbsHandle.createBreadcrumbsByPath(path));
            model.addAttribute("path", path);
            model.addAttribute("files", fileService.getFolderObjects(path));
        } else {
            model.addAttribute("chain", BreadcrumbsHandle.createBreadcrumbsByPath(userRoot));
            model.addAttribute("files", fileService.getFolderObjects(userRoot));
        }
        model.addAttribute("rootFolderPath", userRoot);
        model.addAttribute("currentPath", currentPath);
        model.addAttribute("User", userService.getCurrentUser());
        return "home";
    }



}
