package com.storage.controllers;

import com.storage.dto.FileInfoDto;
import com.storage.model.FileInfo;
import com.storage.security.CustomUserDetails;
import com.storage.services.StringOperation;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final FileService fileService;
    private final UserService userService;
    private final String userRootFolder = "user-%d-files/";

    @Autowired
    public SearchController(FileService fileService, UserService userService)
    {
        this.fileService = fileService;
        this.userService = userService;
    }


    @GetMapping
    public String search(@RequestParam("query") String query, Model model) {
        model.addAttribute("User", getCurrentUser());
        model.addAttribute("rootFolderPath", String.format(userRootFolder, getCurrentUser().getUser().getId()));
        System.out.println("Зашел в серч");
        try {
            System.out.println(query);
            // Validate query
            if (query == null || query.isEmpty()) {
                model.addAttribute("error", "Query cannot be empty");
                return "search"; // Render the same page with an error message
            }

            // Perform search
            ArrayList<FileInfoDto> arrObjects = fileService.getObjectsByQuery(query);

            if (arrObjects.isEmpty()) {
                model.addAttribute("error", "No files found");
                return "search";
            }

            // Populate model
            model.addAttribute("files", arrObjects);
            return "search";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during the search: " + e.getMessage());
            return "search";
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }

}
