package com.storage.controllers;

import com.storage.services.StringOperation;
import com.storage.services.minio.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final FileService fileService;
    private final StringOperation stringOperation;

    @Autowired
    public SearchController(FileService fileService, StringOperation stringOperation)
    {
        this.fileService = fileService;
        this.stringOperation = stringOperation;
    }


    @GetMapping("/")
    public String search(@RequestParam("query") String query, Model model)
    {
        //Обработка ошибок пустого поля
        if(query.isEmpty() || query == null)
        {
            model.addAttribute("error", "Query suposed to be not empty");
            return "redirect:/";
        }


    }
}
