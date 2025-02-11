package com.storage.controllers;

import com.storage.dto.FileInfoDto;
import com.storage.exceptions.PermissionDeniedException;
import com.storage.security.CustomUserDetails;
import com.storage.services.StringOperation;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
public class FileOperationController {


    private final FileService fileService;
    private final UserService userService;
    private final StringOperation stringOperation;
    private final String userRoot = "user-%d-files";
    @Autowired
    public FileOperationController(FileService fileService, UserService userService, StringOperation stringOperation) {
        this.fileService = fileService;
        this.userService = userService;
        this.stringOperation = stringOperation;
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

    @PostMapping("/renameFile")
    public String renameFile(@RequestParam("fullPath") String fullPath, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) {
        try {
            if (!fullPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
                throw new PermissionDeniedException("You don't have access to manage this file");
            }

            String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
            fileService.renameFile(getCurrentUser().getUser().getId(), newName, fullPath);
            redirectAttributes.addFlashAttribute("successMessage", "File renamed successfully");

            String encodedDirectoryPath = UriUtils.encodePath(directoryPath, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        } catch (PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            String encodedDirectoryPath = UriUtils.encodePath(fullPath.substring(0, fullPath.lastIndexOf('/')), StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        }
    }
    @PostMapping("/deleteFolder")
    public String removeFolder(@RequestParam("folderPath") String folderPath, RedirectAttributes redirectAttributes) {
        try {
            if (!folderPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
                throw new PermissionDeniedException("You don't have access to manage this folder");
            }

            String pathToRedirect = folderPath.substring(0, folderPath.substring(0, folderPath.lastIndexOf('/') - 1).lastIndexOf('/'));
            fileService.removeFolder(folderPath);

            redirectAttributes.addFlashAttribute("successMessage", "Folder removed successfully");

            String encodedDirectoryPath = UriUtils.encodePath(pathToRedirect, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        } catch (PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            String encodedDirectoryPath = UriUtils.encodePath(folderPath.substring(0, folderPath.lastIndexOf('/')), StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        }
    }

    @PostMapping("/renameFolder")
    public String renameFolder(@RequestParam("fullPath") String fullPath, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) {
        try {
            if (!fullPath.contains(String.format(userRoot, getCurrentUser().getUser().getId()))) {
                throw new PermissionDeniedException("You don't have access to manage this folder");
            }

            String redirect = stringOperation.getParentPath(fullPath);
            fileService.renameFolder(fullPath, newName);

            String encodedDirectoryPath = UriUtils.encodePath(redirect, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath;
        } catch (PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            String encodedDirectoryPath = UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath;
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
