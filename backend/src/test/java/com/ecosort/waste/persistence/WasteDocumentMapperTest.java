package com.ecosort.waste.persistence;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.persistence.document.WasteDocument;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class WasteDocumentMapperTest {

    private final WasteDocumentMapper mapper = new WasteDocumentMapper();

    @Test
    void toDocumentMapsAllFields() {
        WasteRecord record = new WasteRecord(
            "id-1",
            "user-1",
            "image.jpg",
            "plastic",
            0.95,
            WasteStatus.CLASSIFIED,
            Instant.parse("2024-01-01T00:00:00Z"),
            null
        );

        WasteDocument doc = mapper.toDocument(record);

        assertThat(doc.getId()).isEqualTo("id-1");
        assertThat(doc.getUserId()).isEqualTo("user-1");
        assertThat(doc.getInputRef()).isEqualTo("image.jpg");
        assertThat(doc.getPredictedCategory()).isEqualTo("plastic");
        assertThat(doc.getConfidence()).isEqualTo(0.95);
        assertThat(doc.getStatus()).isEqualTo("CLASSIFIED");
        assertThat(doc.getCreatedAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void toDomainReconstructsEnum() {
        WasteDocument doc = new WasteDocument(
            "id-1",
            "user-1",
            "image.jpg",
            "organic",
            0.8,
            "PENDING",
            Instant.parse("2024-01-01T00:00:00Z"),
            null
        );

        WasteRecord record = mapper.toDomain(doc);

        assertThat(record.id()).isEqualTo("id-1");
        assertThat(record.status()).isEqualTo(WasteStatus.PENDING);
        assertThat(record.predictedCategory()).isEqualTo("organic");
        assertThat(record.confidence()).isEqualTo(0.8);
    }

    @Test
    void roundTripPreservesData() {
        WasteRecord original = WasteRecord.create("user-1", "image.jpg");
        WasteDocument doc = mapper.toDocument(original);
        WasteRecord roundTripped = mapper.toDomain(doc);

        assertThat(roundTripped.id()).isEqualTo(original.id());
        assertThat(roundTripped.userId()).isEqualTo(original.userId());
        assertThat(roundTripped.inputRef()).isEqualTo(original.inputRef());
        assertThat(roundTripped.status()).isEqualTo(original.status());
        assertThat(roundTripped.createdAt()).isEqualTo(original.createdAt());
    }
}
