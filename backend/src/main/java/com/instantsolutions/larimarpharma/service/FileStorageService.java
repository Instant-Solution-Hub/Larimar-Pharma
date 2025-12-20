package com.instantsolutions.larimarpharma.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/competitive-reports/";

    public String storeFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String fileName =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/files/competitive-reports/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
