package com.instantsolutions.larimarpharma.service;


import com.instantsolutions.larimarpharma.entity.VisualAid;
import com.instantsolutions.larimarpharma.repository.VisualAidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisualAidService {

    private final VisualAidRepository visualAidRepository;
    private final FileStorageService fileStorageService;

    private static final String FOLDER = "visual-aids";

    public VisualAid uploadVisualAid(String name, String category, MultipartFile file) {

        String fileUrl = fileStorageService.storeFile(file, FOLDER);

        VisualAid visualAid = VisualAid.builder()
                .name(name)
                .category(category)
                .fileUrl(fileUrl)
                .uploadedAt(LocalDateTime.now())
                .build();

        return visualAidRepository.save(visualAid);
    }

    public void deleteVisualAid(Long id) {

        VisualAid visualAid = visualAidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visual Aid not found"));

        // Delete physical file
        try {
            Path filePath = Paths.get("uploads")
                    .resolve(visualAid.getFileUrl().replace("/files/", ""));
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }

        visualAidRepository.delete(visualAid);
    }

    public List<VisualAid> getVisualAidsByCategory(String category) {
        return visualAidRepository.findByCategoryIgnoreCase(category);
    }

}
