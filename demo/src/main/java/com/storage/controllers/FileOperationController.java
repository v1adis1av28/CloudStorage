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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String deleteFile(@RequestParam("fullPath") String fullPath) throws PermissionDeniedException {
        if (!fullPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
            throw new PermissionDeniedException("You don't have access to manage files");
        }

        fileService.removeFile(fullPath);

        String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
        if (directoryPath.isEmpty()) {
            directoryPath = String.format(userRoot, getCurrentUser().getUser().getId());
        }

        return "redirect:/hello?path=" + directoryPath + "/";
    }

    @PostMapping("renameFile")
    public String renameFile(@RequestParam("fullPath") String fullPath, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) throws Exception {

        if (!fullPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
            throw new PermissionDeniedException("You don't have access to manage this file");
        }
        String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/'));

        fileService.renameFile(getCurrentUser().getUser().getId(),newName, fullPath);
        redirectAttributes.addFlashAttribute("successMessage", "File renamed successfully");

        return "redirect:/hello?path=" + directoryPath + "/";
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
