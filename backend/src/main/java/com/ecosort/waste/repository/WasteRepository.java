package com.ecosort.waste.repository;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;

import java.util.List;

public interface WasteRepository {
    WasteRecord save(WasteRecord record);
    WasteRecord findById(String id);
    List<WasteRecord> findByUserId(String userId);
    List<WasteRecord> findByUserIdAndStatus(String userId, WasteStatus status);
}
