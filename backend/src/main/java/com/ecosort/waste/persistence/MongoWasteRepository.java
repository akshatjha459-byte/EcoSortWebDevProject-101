package com.ecosort.waste.persistence;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.repository.WasteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoWasteRepository implements WasteRepository {

    private final WasteMongoRepository mongoRepo;
    private final WasteDocumentMapper mapper;

    public MongoWasteRepository(WasteMongoRepository mongoRepo, WasteDocumentMapper mapper) {
        this.mongoRepo = mongoRepo;
        this.mapper = mapper;
    }

    @Override
    public WasteRecord save(WasteRecord record) {
        try {
            var document = mapper.toDocument(record);
            var saved = mongoRepo.save(document);
            return mapper.toDomain(saved);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to persist waste record", ex);
        }
    }

    @Override
    public WasteRecord findById(String id) {
        return mongoRepo.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<WasteRecord> findByUserId(String userId) {
        return mongoRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WasteRecord> findByUserIdAndStatus(String userId, WasteStatus status) {
        return mongoRepo.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status.name()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
