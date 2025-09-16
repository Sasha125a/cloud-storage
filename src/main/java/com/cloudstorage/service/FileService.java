// FileService.java
package com.cloudstorage.service;

import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    
    private final Path rootLocation = Paths.get("uploads");
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private UserRepository userRepository;

    public void saveFile(MultipartFile file, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storageFilename = UUID.randomUUID() + fileExtension;
            
            Files.createDirectories(rootLocation);
            Files.copy(file.getInputStream(), rootLocation.resolve(storageFilename));
            
            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalFilename(originalFilename);
            fileEntity.setFilename(storageFilename);
            fileEntity.setFileType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setStoragePath(rootLocation.resolve(storageFilename).toString());
            fileEntity.setUser(user);
            fileEntity.setUploadedAt(LocalDateTime.now());
            
            fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource loadFile(Long fileId, String username) {
        FileEntity fileEntity = fileRepository.findByIdAndUserUsername(fileId, username)
                .orElseThrow(() -> new RuntimeException("File not found"));
        
        try {
            Path file = rootLocation.resolve(fileEntity.getFilename());
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public List<FileEntity> getUserFiles(String username) {
        return fileRepository.findByUserUsername(username);
    }

    public void deleteFile(Long fileId, String username) {
        FileEntity fileEntity = fileRepository.findByIdAndUserUsername(fileId, username)
                .orElseThrow(() -> new RuntimeException("File not found"));
        
        try {
            Files.deleteIfExists(rootLocation.resolve(fileEntity.getFilename()));
            fileRepository.delete(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
