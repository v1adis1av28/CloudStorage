package com.storage.controllers;


import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Controller
public class UploadingController {

    private final FileService fileService;
    private final UserService userService;

    @Autowired
    public UploadingController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        if(file.isEmpty()) {
            model.addAttribute("error", "File is empty");
            return "redirect:/hello";
        }

        try {fileService.uploadFile(getCurrentUser().getUser().getId(),"", file.getInputStream(),file.getResource().getFilename(),file.getContentType());}
        catch (IllegalArgumentException exception)
        {
            model.addAttribute("error", exception.getMessage());
            return "redirect:/hello";
        }
        return "redirect:/hello";
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(@RequestParam("files") MultipartFile[] files,
                               Model model) throws Exception {
        if (files.length == 0) {
            model.addAttribute("error", "Folder is empty");
            return "redirect:/hello";
        }
        try {
            fileService.uploadFolder(getCurrentUser().getUser().getId(), "testUploadFolder/", files);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            return "redirect:/hello";
        }
        return "redirect:/hello";
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
