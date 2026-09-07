package com.ecosort.waste.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "waste_records")
@CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}")
@CompoundIndex(name = "user_status_idx", def = "{'userId': 1, 'status': 1}")
public class WasteDocument {

    @Id
    private String id;
    private String userId;
    private String inputRef;
    private String predictedCategory;
    private Double confidence;
    private String status;
    private Instant createdAt;
    private String errorMessage;

    public WasteDocument() {
    }

    public WasteDocument(String id, String userId, String inputRef, String predictedCategory, Double confidence, String status, Instant createdAt, String errorMessage) {
        this.id = id;
        this.userId = userId;
        this.inputRef = inputRef;
        this.predictedCategory = predictedCategory;
        this.confidence = confidence;
        this.status = status;
        this.createdAt = createdAt;
        this.errorMessage = errorMessage;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getInputRef() {
        return inputRef;
    }

    public String getPredictedCategory() {
        return predictedCategory;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
