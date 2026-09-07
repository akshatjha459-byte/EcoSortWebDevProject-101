package com.ecosort.classification.inference;

public class ClassificationInferenceException extends RuntimeException {
    public ClassificationInferenceException(String message) {
        super(message);
    }

    public ClassificationInferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}