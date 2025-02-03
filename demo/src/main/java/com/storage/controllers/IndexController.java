package com.storage.controllers;

import com.storage.security.CustomUserDetails;
import com.storage.services.UserService;
import com.storage.services.minio.FileService;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class IndexController {

    private final FileService fileService;
    private final UserService userService;
    @Autowired
    public IndexController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @SneakyThrows
    @GetMapping("/hello")
    public String index() {
        //fileService.renameFile(user.getUser().getId(),"miniolaunnch123","user-20-files/test/minioLaunch");
        //fileService.removeFile("user-files/user-20-files/test/");
        //fileService.renameFolder("user-20-files/test/","newfoldername");
        //fileService.uploadFileTest(user.getUser().getId(),"user-20-files/test/minioLaunch","G:\\CloudStorage\\demo\\src\\main\\resources\\minioLaunch.txt","text/plain");
        //fileService.removeFolder("user-20-files/test/test/");
        return "home";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        if(file.isEmpty()) {
            model.addAttribute("error", "File is empty");
            return "home";
        }

        try {fileService.uploadFile(getCurrentUser().getUser().getId(),"testUpload/", file.getInputStream(),file.getResource().getFilename(),file.getContentType());}
        catch (IllegalArgumentException exception)
        {
            model.addAttribute("error", exception.getMessage());
            return "home";
        }
        return "home";
    }


    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsername(auth.getName());
        return user;
    }
}
