package com.storage.controllers;

import com.storage.dto.FileInfoDto;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

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
        model.addAttribute("User", userService.getCurrentUser());
        model.addAttribute("rootFolderPath", String.format(userRootFolder,userService.getCurrentUserId()));

        try {
            if (query == null || query.isEmpty()) {
                model.addAttribute("error", "Query cannot be empty");
                return "search";
            }

            ArrayList<FileInfoDto> arrObjects = fileService.getObjectsByQuery(query);

            if (arrObjects.isEmpty()) {
                model.addAttribute("error", "No files found matching your query.");
                return "search";
            }

            model.addAttribute("files", arrObjects);
            return "search";

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during the search: " + e.getMessage());
            return "search";
        }
    }

}
