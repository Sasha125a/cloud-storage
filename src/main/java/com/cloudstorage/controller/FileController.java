package com.cloudstorage.controller;

import com.cloudstorage.model.FileEntity;
import com.cloudstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileService fileService;

    @GetMapping
    public String listFiles(Model model, Principal principal) {
        List<FileEntity> files = fileService.getUserFiles(principal.getName());
        model.addAttribute("files", files);
        return "files";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, 
                           Principal principal) {
        fileService.saveFile(file, principal.getName());
        return "redirect:/files";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, 
                                               Principal principal) {
        Resource file = fileService.loadFile(id, principal.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, Principal principal) {
        fileService.deleteFile(id, principal.getName());
        return "redirect:/files";
    }
}
