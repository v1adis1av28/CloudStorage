package com.storage.controllers;

import com.storage.exceptions.PermissionDeniedException;
import com.storage.services.StringOperation;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;


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


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("currentPath") String currentPath, RedirectAttributes redirectAttributes) {
        try {
            if(!currentPath.isEmpty()) {
                if (!stringOperation.rightsVerification(currentPath, String.format(userRootFolder, userService.getCurrentUserId()))) {
                    throw new PermissionDeniedException("У вас нет возможности загрузить файл в данную директорию");
                }
            }

            if (file == null || file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Файл не выбран или пустой.");
                return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
            }

            String normalizedFolderPath = !currentPath.isEmpty() ? UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name()) : String.format(userRootFolder, userService.getCurrentUserId());

            fileService.uploadFile(normalizedFolderPath, file.getInputStream(), file.getOriginalFilename(), file.getContentType());

            String encodedDirectoryPath = UriUtils.encodePath(
                    normalizedFolderPath.substring(0, Math.max(0, normalizedFolderPath.lastIndexOf('/'))),
                    StandardCharsets.UTF_8.name()
            );
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }
        catch (PermissionDeniedException e)
        {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет доступа к изменению этой папки");
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при загрузке файла: " + e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
        }
    }

    @PostMapping("/uploadFolder")
    public String uploadFolder(@RequestParam("files") MultipartFile[] files, @RequestParam("currentPath") String currentPath, RedirectAttributes redirectAttributes) {
        try {

            if (currentPath.isEmpty()) {
                String folderPath = String.format(userRootFolder, userService.getCurrentUserId());
                fileService.uploadFolder(folderPath, files);
                return "redirect:/hello?path=" + UriUtils.encodePath(folderPath, StandardCharsets.UTF_8.name());
            }

            if (!stringOperation.rightsVerification(currentPath, String.format(userRootFolder, userService.getCurrentUserId()))) {
                throw new PermissionDeniedException("У вас нет доступа для изменения этой директории!");
            }


            if (files.length == 0) {
                if (currentPath == null || currentPath.isEmpty()) {
                    throw new IllegalArgumentException("Текущий путь обязателен для создания папки.");
                }
                if (!currentPath.contains(String.format(userRootFolder, userService.getCurrentUserId()))) {
                    throw new PermissionDeniedException("У вас нет доступа к управлению этой папкой.");
                }
                fileService.createEmptyFolder(userService.getCurrentUserId(), currentPath);
                String encodedDirectoryPath = UriUtils.encodePath(currentPath.substring(0, currentPath.lastIndexOf('/')), StandardCharsets.UTF_8.name());
                return "redirect:/hello?path=" + encodedDirectoryPath;
            }

            fileService.uploadFolder(currentPath, files);
        } catch (IllegalArgumentException | PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при загрузке папки: " + e.getMessage());
        }
        return "redirect:/hello?path=" + UriUtils.encodePath(currentPath, StandardCharsets.UTF_8.name());
    }

}
