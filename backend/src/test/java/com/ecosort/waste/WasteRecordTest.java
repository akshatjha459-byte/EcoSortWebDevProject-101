package com.ecosort.waste;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WasteRecordTest {

    @Test
    void createGeneratesIdAndPendingStatus() {
        WasteRecord record = WasteRecord.create("user-1", "image-1.jpg");

        assertThat(record.id()).isNotNull().isNotBlank();
        assertThat(record.userId()).isEqualTo("user-1");
        assertThat(record.inputRef()).isEqualTo("image-1.jpg");
        assertThat(record.status()).isEqualTo(WasteStatus.PENDING);
        assertThat(record.createdAt()).isNotNull();
        assertThat(record.predictedCategory()).isNull();
        assertThat(record.confidence()).isNull();
        assertThat(record.errorMessage()).isNull();
    }

    @Test
    void rejectsMissingUserId() {
        assertThatThrownBy(() -> WasteRecord.create(null, "image.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID is required");
    }

    @Test
    void rejectsMissingInputRef() {
        assertThatThrownBy(() -> WasteRecord.create("user-1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Input reference is required");
    }

    @Test
    void rejectsInvalidConfidence() {
        WasteRecord base = WasteRecord.create("user-1", "image.jpg");
        assertThatThrownBy(() -> base.withClassification("plastic", -0.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Confidence must be between 0 and 1");
    }

    @Test
    void withClassificationUpdatesFields() {
        WasteRecord base = WasteRecord.create("user-1", "image.jpg");
        WasteRecord classified = base.withClassification("plastic", 0.92);

        assertThat(classified.status()).isEqualTo(WasteStatus.CLASSIFIED);
        assertThat(classified.predictedCategory()).isEqualTo("plastic");
        assertThat(classified.confidence()).isEqualTo(0.92);
        assertThat(classified.userId()).isEqualTo("user-1");
        assertThat(classified.id()).isEqualTo(base.id());
    }

    @Test
    void withErrorUpdatesStatusAndMessage() {
        WasteRecord base = WasteRecord.create("user-1", "image.jpg");
        WasteRecord failed = base.withError("AI service unavailable");

        assertThat(failed.status()).isEqualTo(WasteStatus.FAILED);
        assertThat(failed.errorMessage()).isEqualTo("AI service unavailable");
        assertThat(failed.userId()).isEqualTo("user-1");
    }

    @Test
    void rejectsClassifiedWithoutCategory() {
        WasteRecord base = WasteRecord.create("user-1", "image.jpg");
        assertThatThrownBy(() -> base.withClassification(null, 0.9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Predicted category is required for classified records");
    }
}
