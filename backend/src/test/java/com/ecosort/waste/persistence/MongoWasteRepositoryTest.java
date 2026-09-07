package com.ecosort.waste.persistence;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.persistence.document.WasteDocument;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MongoWasteRepositoryTest {

    @Test
    void wasteDocumentIsMappedToCorrectCollection() {
        Document doc = WasteDocument.class.getAnnotation(Document.class);
        assertThat(doc).isNotNull();
        assertThat(doc.collection()).isEqualTo("waste_records");
    }

    @Test
    void wasteDocumentHasCompoundUserIdCreatedAtIndex() {
        CompoundIndexes indexes = WasteDocument.class.getAnnotation(CompoundIndexes.class);
        assertThat(indexes).isNotNull();
        assertThat(indexes.value()).isNotEmpty();

        boolean foundUserIdCreatedAt = false;
        for (CompoundIndex index : indexes.value()) {
            if (index.name().equals("user_created_idx")) {
                foundUserIdCreatedAt = true;
            }
        }
        assertThat(foundUserIdCreatedAt).isTrue();
    }

    @Test
    void wasteDocumentHasCompoundUserIdStatusIndex() {
        CompoundIndexes indexes = WasteDocument.class.getAnnotation(CompoundIndexes.class);
        assertThat(indexes).isNotNull();

        boolean foundUserIdStatus = false;
        for (CompoundIndex index : indexes.value()) {
            if (index.name().equals("user_status_idx")) {
                foundUserIdStatus = true;
            }
        }
        assertThat(foundUserIdStatus).isTrue();
    }

    @Test
    void findByUserIdReturnsRecords() {
        WasteMongoRepository mongoRepo = mock(WasteMongoRepository.class);
        MongoWasteRepository repository = new MongoWasteRepository(mongoRepo, new WasteDocumentMapper());

        WasteRecord record = WasteRecord.create("user-1", "image.jpg");
        WasteDocument doc = new WasteDocument("id-1", "user-1", "image.jpg", null, null, "PENDING", Instant.now(), null);
        when(mongoRepo.findByUserIdOrderByCreatedAtDesc("user-1")).thenReturn(List.of(doc));

        List<WasteRecord> results = repository.findByUserId("user-1");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).userId()).isEqualTo("user-1");
        assertThat(results.get(0).status()).isEqualTo(WasteStatus.PENDING);
        verify(mongoRepo).findByUserIdOrderByCreatedAtDesc("user-1");
    }

    @Test
    void findByUserIdAndStatusReturnsFilteredRecords() {
        WasteMongoRepository mongoRepo = mock(WasteMongoRepository.class);
        MongoWasteRepository repository = new MongoWasteRepository(mongoRepo, new WasteDocumentMapper());

        WasteDocument doc = new WasteDocument("id-1", "user-1", "image.jpg", "plastic", 0.9, "CLASSIFIED", Instant.now(), null);
        when(mongoRepo.findByUserIdAndStatusOrderByCreatedAtDesc("user-1", "CLASSIFIED")).thenReturn(List.of(doc));

        List<WasteRecord> results = repository.findByUserIdAndStatus("user-1", WasteStatus.CLASSIFIED);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).predictedCategory()).isEqualTo("plastic");
        assertThat(results.get(0).confidence()).isEqualTo(0.9);
    }

    @Test
    void savePersistsRecordThroughMongoRepository() {
        WasteMongoRepository mongoRepo = mock(WasteMongoRepository.class);
        WasteDocumentMapper mapper = new WasteDocumentMapper();
        MongoWasteRepository repository = new MongoWasteRepository(mongoRepo, mapper);

        WasteRecord record = WasteRecord.create("user-1", "image.jpg");
        WasteDocument savedDoc = mapper.toDocument(record);
        when(mongoRepo.save(any(WasteDocument.class))).thenReturn(savedDoc);

        WasteRecord result = repository.save(record);

        assertThat(result.id()).isEqualTo(record.id());
        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.status()).isEqualTo(WasteStatus.PENDING);
        verify(mongoRepo).save(any(WasteDocument.class));
    }

    @Test
    void saveWrapsPersistenceException() {
        WasteMongoRepository mongoRepo = mock(WasteMongoRepository.class);
        MongoWasteRepository repository = new MongoWasteRepository(mongoRepo, new WasteDocumentMapper());

        when(mongoRepo.save(any())).thenThrow(new RuntimeException("connection refused"));

        WasteRecord record = WasteRecord.create("user-1", "image.jpg");

        assertThatThrownBy(() -> repository.save(record))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to persist waste record");
    }

    @Test
    void findByIdReturnsNullWhenRecordDoesNotExist() {
        WasteMongoRepository mongoRepo = mock(WasteMongoRepository.class);
        MongoWasteRepository repository = new MongoWasteRepository(mongoRepo, new WasteDocumentMapper());

        when(mongoRepo.findById("missing")).thenReturn(Optional.empty());

        WasteRecord result = repository.findById("missing");

        assertThat(result).isNull();
        verify(mongoRepo).findById("missing");
    }
}
