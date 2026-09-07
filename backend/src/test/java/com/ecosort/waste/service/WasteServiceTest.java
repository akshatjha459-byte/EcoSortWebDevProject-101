package com.ecosort.waste.service;

import com.ecosort.waste.model.WasteRecord;
import com.ecosort.waste.model.WasteStatus;
import com.ecosort.waste.repository.WasteRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WasteServiceTest {

    @Test
    void submitCreatesPendingRecord() {
        WasteRepository repo = mock(WasteRepository.class);
        WasteService service = new WasteService(repo);

        WasteRecord saved = WasteRecord.create("user-1", "image.jpg");
        when(repo.save(any(WasteRecord.class))).thenReturn(saved);

        WasteRecord result = service.submit("user-1", "image.jpg");

        assertThat(result.status()).isEqualTo(WasteStatus.PENDING);
        assertThat(result.userId()).isEqualTo("user-1");
        verify(repo).save(any(WasteRecord.class));
    }

    @Test
    void completeClassificationUpdatesRecord() {
        WasteRepository repo = mock(WasteRepository.class);
        WasteService service = new WasteService(repo);

        WasteRecord existing = WasteRecord.create("user-1", "image.jpg");
        WasteRecord classified = existing.withClassification("plastic", 0.92);

        when(repo.findById("id-1")).thenReturn(existing);
        when(repo.save(classified)).thenReturn(classified);

        WasteRecord result = service.completeClassification("id-1", "plastic", 0.92);

        assertThat(result.status()).isEqualTo(WasteStatus.CLASSIFIED);
        assertThat(result.predictedCategory()).isEqualTo("plastic");
        assertThat(result.confidence()).isEqualTo(0.92);
        verify(repo).save(classified);
    }

    @Test
    void markFailedUpdatesRecord() {
        WasteRepository repo = mock(WasteRepository.class);
        WasteService service = new WasteService(repo);

        WasteRecord existing = WasteRecord.create("user-1", "image.jpg");
        WasteRecord failed = existing.withError("Service error");

        when(repo.findById("id-1")).thenReturn(existing);
        when(repo.save(failed)).thenReturn(failed);

        WasteRecord result = service.markFailed("id-1", "Service error");

        assertThat(result.status()).isEqualTo(WasteStatus.FAILED);
        assertThat(result.errorMessage()).isEqualTo("Service error");
        verify(repo).save(failed);
    }

    @Test
    void throwsWhenRecordNotFound() {
        WasteRepository repo = mock(WasteRepository.class);
        WasteService service = new WasteService(repo);

        when(repo.findById("missing")).thenReturn(null);

        assertThatThrownBy(() -> service.completeClassification("missing", "x", 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Waste record not found");
    }
}
