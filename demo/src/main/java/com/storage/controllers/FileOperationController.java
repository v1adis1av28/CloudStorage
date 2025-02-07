package com.storage.controllers;

import com.storage.dto.FileInfoDto;
import com.storage.exceptions.PermissionDeniedException;
import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileOperationController {


    private final FileService fileService;
    private final UserService userService;
    private final String userRoot = "user-%d-files";
    @Autowired
    public FileOperationController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("fullPath") String path) throws PermissionDeniedException {

        if(path.contains(String.format(userRoot, getCurrentUser().getUser().getId())))
        {
           fileService.removeFile(path);
        }
        else
        {
            //обработка случая удаления файлов из не своей папки
            throw new PermissionDeniedException("You dont have acces to manage files");
        }
        return "redirect:/hello";
    }
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
