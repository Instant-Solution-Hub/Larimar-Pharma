package com.instantsolutions.larimarpharma.controller;


import com.instantsolutions.larimarpharma.entity.VisualAid;
import com.instantsolutions.larimarpharma.service.VisualAidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/visual-aids")
@RequiredArgsConstructor
public class VisualAidController {

    private final VisualAidService visualAidService;

    @PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisualAid> uploadVisualAid(
            @Valid @RequestPart("name") String name,
            @Valid @RequestPart("category") String category,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                visualAidService.uploadVisualAid(name, category, file)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisualAid(@PathVariable Long id) {
        visualAidService.deleteVisualAid(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<VisualAid>> getByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                visualAidService.getVisualAidsByCategory(category)
        );
    }

}
