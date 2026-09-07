package com.ecosort.waste.persistence;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.persistence.document.WasteDocument;
import org.springframework.stereotype.Component;

@Component
public class WasteDocumentMapper {

    public WasteDocument toDocument(WasteRecord record) {
        return new WasteDocument(
            record.id(),
            record.userId(),
            record.inputRef(),
            record.predictedCategory(),
            record.confidence(),
            record.status() != null ? record.status().name() : null,
            record.createdAt(),
            record.errorMessage()
        );
    }

    public WasteRecord toDomain(WasteDocument document) {
        return new WasteRecord(
            document.getId(),
            document.getUserId(),
            document.getInputRef(),
            document.getPredictedCategory(),
            document.getConfidence(),
            document.getStatus() != null ? WasteStatus.valueOf(document.getStatus()) : null,
            document.getCreatedAt(),
            document.getErrorMessage()
        );
    }
}
