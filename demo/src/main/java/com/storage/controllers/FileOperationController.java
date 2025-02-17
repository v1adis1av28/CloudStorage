package com.storage.controllers;

import com.storage.exceptions.ImpossibleToChangeException;
import com.storage.exceptions.PermissionDeniedException;
import com.storage.security.CustomUserDetails;
import com.storage.services.StringOperation;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
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
    public String deleteFile(@RequestParam("fullPath") String fullPath, RedirectAttributes redirectAttributes) {
        try {
            if (!fullPath.contains(String.format(userRoot, userService.getCurrentUserId()))) {
                throw new PermissionDeniedException("You don't have permission to manage this file.");
            }
            fileService.removeFile(fullPath);
            String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
            if (directoryPath.isEmpty()) {
                directoryPath = String.format(userRoot, userService.getCurrentUserId());
            }
            return "redirect:/hello?path=" + UriUtils.encodePath(directoryPath, StandardCharsets.UTF_8.name()) + "/";
        } catch (PermissionDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the file.");
            return "redirect:/hello?path=" + UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
        }
    }

    @PostMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fullPath") String fullPath){

        InputStream inputStream = fileService.downloadFile(fullPath);
        String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);

        String contentDispositionHeader = stringOperation.encodeFilenameForDownload(fileName);

        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/renameFile")
    public String renameFile(@RequestParam("fullPath") String fullPath, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) {
        try {
            if (!fullPath.contains(String.format(userRoot, userService.getCurrentUserId())) || stringOperation.isValidInputField(newName)) {
                throw new IllegalArgumentException("Invalid file path or new name.");
            }
            String directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
            fileService.renameFile(userService.getCurrentUserId(), newName, fullPath);
            return "redirect:/hello?path=" + UriUtils.encodePath(directoryPath, StandardCharsets.UTF_8.name()) + "/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while renaming the file.");
            return "redirect:/hello?path=" + UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
        }
    }
    @PostMapping("/deleteFolder")
    public String removeFolder(@RequestParam("folderPath") String folderPath, RedirectAttributes redirectAttributes) {

        try {
            if (folderPath.equals(String.format(userRoot,userService.getCurrentUserId()) + "/") || !folderPath.contains(String.format(userRoot, userService.getCurrentUserId()))) {
                throw new ImpossibleToChangeException("Нет возможности удалить данную папку!");
            }
            String pathToRedirect = folderPath.substring(0, folderPath.substring(0, folderPath.lastIndexOf('/') - 1).lastIndexOf('/'));
            fileService.removeFolder(folderPath);

            redirectAttributes.addFlashAttribute("successMessage", "Folder removed successfully");

            String encodedDirectoryPath = UriUtils.encodePath(pathToRedirect, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath + "/";
        }
        catch (ImpossibleToChangeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(folderPath, StandardCharsets.UTF_8.name());
        }
    }

    @PostMapping("/renameFolder")
    public String renameFolder(@RequestParam("fullPath") String fullPath, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) {
        try {
            if (!fullPath.contains(String.format(userRoot, userService.getCurrentUserId())) || stringOperation.isValidInputField(newName)) {
                throw new ImpossibleToChangeException("Invalid folder path or new name.");
            }
            String redirect = stringOperation.getParentPath(fullPath);
            fileService.renameFolder(fullPath, newName);

            String encodedDirectoryPath = UriUtils.encodePath(redirect, StandardCharsets.UTF_8.name());
            return "redirect:/hello?path=" + encodedDirectoryPath;
        } catch (ImpossibleToChangeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/hello?path=" + UriUtils.encodePath(fullPath, StandardCharsets.UTF_8.name());
        }
    }

}
