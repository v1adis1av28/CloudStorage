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
import org.springframework.web.HttpRequestHandler;
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
    public String uploadFolder(@RequestParam("files") MultipartFile[] files, Model model) throws Exception {
        if (files.length == 0) {
            //TODO загрузка пустой папки
//            try {
//                if (!folderPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
//                    throw new PermissionDeniedException("You don't have access to manage this folder");
//                }
//
//                fileService.createEmptyFolder(getCurrentUser().getUser().getId(), folderPath);
//                redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully");
//
//                String encodedDirectoryPath = UriUtils.encodePath(folderPath.substring(0, folderPath.lastIndexOf('/')), StandardCharsets.UTF_8.name());
//                return "redirect:/hello?path=" + encodedDirectoryPath + "/";
//            } catch (Exception e) {
//                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//                return "redirect:/hello";
//            }
            //fileService.createEmptyFolder(getCurrentUser().getUser().getId(), folderPath);
            return "redirect:/hello";
        }
        try {
            fileService.uploadFolder(getCurrentUser().getUser().getId(), "", files);
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
