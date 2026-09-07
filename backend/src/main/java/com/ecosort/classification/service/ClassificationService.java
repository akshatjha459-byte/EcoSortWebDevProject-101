package com.ecosort.classification.service;

import com.ecosort.auth.model.User;
import com.ecosort.classification.inference.ClassificationInference;
import com.ecosort.classification.inference.ClassificationInferenceException;
import com.ecosort.classification.inference.ClassificationResult;
import com.ecosort.security.CurrentUser;
import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.repository.WasteRepository;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Service
@Profile("classification")
public class ClassificationService {

    private final WasteRepository wasteRepository;
    private final ClassificationInference classificationInference;

    public ClassificationService(WasteRepository wasteRepository, ClassificationInference classificationInference) {
        this.wasteRepository = wasteRepository;
        this.classificationInference = classificationInference;
    }

    public ClassificationResult classify(String wasteRecordId) {
        // Obtain authenticated user
        User currentUser = CurrentUser.get();
        if (currentUser == null) {
            throw new IllegalArgumentException("Unauthenticated user");
        }

        // Validate input
        if (wasteRecordId == null || wasteRecordId.isBlank()) {
            throw new IllegalArgumentException("Waste record ID is required");
        }

        // Load waste record
        WasteRecord record = wasteRepository.findById(wasteRecordId);
        if (record == null) {
            throw new IllegalArgumentException("Waste record not found: " + wasteRecordId);
        }

        // Verify ownership
        if (!record.userId().equals(currentUser.id())) {
            throw new IllegalArgumentException("Access denied: waste record does not belong to current user");
        }

        // Perform inference
        ClassificationResult result;
        try {
            result = classificationInference.infer(record.inputRef());
        } catch (ClassificationInferenceException ex) {
            throw new IllegalStateException("Classification inference failed", ex);
        }

        // Validate inference result
        if (result == null) {
            throw new IllegalStateException("Inference returned null result");
        }
        String predictedCategory = result.getPredictedCategory();
        if (predictedCategory == null || predictedCategory.isBlank()) {
            throw new IllegalArgumentException("Invalid inference result: predicted category is required");
        }
        double confidence = result.getConfidence();
        if (Double.isNaN(confidence) || confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("Invalid inference result: confidence must be between 0.0 and 1.0");
        }

        // Update waste record with classification result
        WasteRecord classifiedRecord = record.withClassification(predictedCategory, confidence);
        wasteRepository.save(classifiedRecord);

        return result;
    }
}