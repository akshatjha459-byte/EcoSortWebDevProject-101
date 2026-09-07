package com.ecosort.waste.service;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.repository.WasteRepository;
import org.springframework.stereotype.Service;

@Service
public class WasteService {

    private final WasteRepository wasteRepository;

    public WasteService(WasteRepository wasteRepository) {
        this.wasteRepository = wasteRepository;
    }

    public WasteRecord submit(String userId, String inputRef) {
        WasteRecord record = WasteRecord.create(userId, inputRef);
        return wasteRepository.save(record);
    }

    public WasteRecord completeClassification(String id, String predictedCategory, double confidence) {
        WasteRecord record = findOrThrow(id);
        return wasteRepository.save(record.withClassification(predictedCategory, confidence));
    }

    public WasteRecord markFailed(String id, String errorMessage) {
        WasteRecord record = findOrThrow(id);
        return wasteRepository.save(record.withError(errorMessage));
    }

    public WasteRecord findById(String id) {
        return findOrThrow(id);
    }

    private WasteRecord findOrThrow(String id) {
        WasteRecord record = wasteRepository.findById(id);
        if (record == null) {
            throw new IllegalArgumentException("Waste record not found: " + id);
        }
        return record;
    }
}
