package com.ecosort.classification.controller;

import com.ecosort.classification.service.ClassificationService;
import com.ecosort.classification.inference.ClassificationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;

@RestController
@Profile("classification")
@RequestMapping("/api/waste")
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @PostMapping("/{id}/classify")
    public ResponseEntity<ClassificationResult> classify(@PathVariable String id) {
        ClassificationResult result = classificationService.classify(id);
        return ResponseEntity.ok(result);
    }
}