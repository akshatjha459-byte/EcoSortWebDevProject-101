package com.ecosort.classification.inference;

public class ClassificationResult {
    private final String predictedCategory;
    private final double confidence;
    // Optional metadata can be added later as needed

    public ClassificationResult(String predictedCategory, double confidence) {
        this.predictedCategory = predictedCategory;
        this.confidence = confidence;
    }

    public String getPredictedCategory() {
        return predictedCategory;
    }

    public double getConfidence() {
        return confidence;
    }
}