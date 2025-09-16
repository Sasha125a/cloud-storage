// FileEntity.java
package com.cloudstorage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String filename;
    private String originalFilename;
    private String fileType;
    private Long size;
    private String storagePath;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDateTime uploadedAt;
    
    // Конструкторы, геттеры и сеттеры
}
