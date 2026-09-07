package com.ecosort.waste.persistence;

import com.ecosort.waste.persistence.document.WasteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WasteMongoRepository extends MongoRepository<WasteDocument, String> {
    List<WasteDocument> findByUserIdOrderByCreatedAtDesc(String userId);
    List<WasteDocument> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
}
