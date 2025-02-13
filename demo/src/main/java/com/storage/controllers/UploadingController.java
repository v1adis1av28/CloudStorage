package com.storage.controllers;


import com.storage.exceptions.PermissionDeniedException;
import com.storage.security.CustomUserDetails;
import com.storage.services.StringOperation;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Controller
public class UploadingController {

    private final FileService fileService;
    private final UserService userService;
    private final String userRootFolder = "user-%d-files/";
    private final StringOperation stringOperation;

    @Autowired
    public UploadingController(FileService fileService, UserService userService, StringOperation stringOperation) {
        this.fileService = fileService;
        this.userService = userService;
        this.stringOperation = stringOperation;
    }


    //TODO Закрыть доступ для загрузки файла и папки не в свою директорию
    @SneakyThrows
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("currentPath") String currentPath, RedirectAttributes redirectAttributes) throws ServerException, InsufficientDataException, ErrorResponseException, IOException {

        if(currentPath.isEmpty())
        {
            String folderPath =String.format(userRootFolder,getCurrentUser().getUser().getId());
            fileService.uploadFile(folderPath,file.getInputStream(),file.getOriginalFilename(),file.getContentType());
            return "redirect:/hello?path="+folderPath;
        }

        if(!stringOperation.rightsVerification(currentPath,String.format(userRootFolder, getCurrentUser().getUser().getId())))
        {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет доступа к изменению этой папки");
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }

        if (file == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Файл не выбран");
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }

        String normalizedFolderPath = !currentPath.isEmpty() ? UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name()) : String.format(userRootFolder,getCurrentUser().getUser().getId());

        try {
            fileService.uploadFile(normalizedFolderPath, file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            String encodedDirectoryPath = UriUtils.encodePath(
                    normalizedFolderPath.substring(0, Math.max(0, normalizedFolderPath.lastIndexOf('/'))),
                    StandardCharsets.UTF_8.name()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Файл успешно загружен.");
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при загрузке файла: " + e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(@RequestParam("files") MultipartFile[] files, @RequestParam("currentPath") String currentPath, RedirectAttributes redirectAttributes) throws Exception {

        try {
            if (currentPath.isEmpty())
            {
                String folderPath =String.format(userRootFolder,getCurrentUser().getUser().getId());
                fileService.uploadFolder(getCurrentUser().getUser().getId(), folderPath, files);
            return "redirect:/hello?path=" + UriUtils.encodePath(folderPath, StandardCharsets.UTF_8.name());
            }
            if(!stringOperation.rightsVerification(currentPath,String.format(userRootFolder, getCurrentUser().getUser().getId())))
            {
                redirectAttributes.addFlashAttribute("errorMessage", "У вас нет доступа к изменению этой папки");
                return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
            }
            if (files.length == 0) {
                if (currentPath == null || currentPath.isEmpty()) {
                    throw new IllegalArgumentException("Current path is required to create a folder");
                }
                if (!currentPath.contains(String.format(userRootFolder, getCurrentUser().getUser().getId()))) {
                    throw new PermissionDeniedException("You don't have access to manage this folder");
                }
                fileService.createEmptyFolder(getCurrentUser().getUser().getId(), currentPath);
                redirectAttributes.addFlashAttribute("successMessage", "Folder created successfully");

                String encodedDirectoryPath = UriUtils.encodePath(currentPath.substring(0, currentPath.lastIndexOf('/')), StandardCharsets.UTF_8.name());
                return "redirect:/hello?path=" + encodedDirectoryPath ;
            } else {
                fileService.uploadFolder(getCurrentUser().getUser().getId(), currentPath, files);
                redirectAttributes.addFlashAttribute("successMessage", "Files uploaded successfully");
            }
        } catch (IllegalArgumentException | PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while uploading the folder");
        }

        return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
