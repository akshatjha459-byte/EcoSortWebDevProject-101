package com.ecosort.waste.model;

import java.time.Instant;
import java.util.UUID;

public record WasteRecord(
    String id,
    String userId,
    String inputRef,
    String predictedCategory,
    Double confidence,
    WasteStatus status,
    Instant createdAt,
    String errorMessage
) {
    public static WasteRecord create(String userId, String inputRef) {
        return new WasteRecord(
            UUID.randomUUID().toString(),
            userId,
            inputRef,
            null,
            null,
            WasteStatus.PENDING,
            Instant.now(),
            null
        );
    }

    public WasteRecord {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (inputRef == null || inputRef.isBlank()) {
            throw new IllegalArgumentException("Input reference is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at is required");
        }
        if (confidence != null && (confidence < 0.0 || confidence > 1.0)) {
            throw new IllegalArgumentException("Confidence must be between 0 and 1");
        }
        if (status == WasteStatus.CLASSIFIED && (predictedCategory == null || predictedCategory.isBlank())) {
            throw new IllegalArgumentException("Predicted category is required for classified records");
        }
    }

    public WasteRecord withClassification(String predictedCategory, double confidence) {
        return new WasteRecord(
            this.id,
            this.userId,
            this.inputRef,
            predictedCategory,
            confidence,
            WasteStatus.CLASSIFIED,
            this.createdAt,
            this.errorMessage
        );
    }

    public WasteRecord withError(String errorMessage) {
        return new WasteRecord(
            this.id,
            this.userId,
            this.inputRef,
            this.predictedCategory,
            this.confidence,
            WasteStatus.FAILED,
            this.createdAt,
            errorMessage
        );
    }
}
