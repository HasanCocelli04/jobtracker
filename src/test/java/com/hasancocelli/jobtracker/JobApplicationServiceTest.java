package com.hasancocelli.jobtracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repository;

    private JobApplicationService service;

    @BeforeEach
    void setUp() {
        service = new JobApplicationService(repository);
    }

    private JobApplication sampleApplication() {
        return new JobApplication("Acme", "Graduate Engineer", ApplicationStatus.APPLIED, LocalDate.of(2026, 9, 28), "Applied online");
    }

    @Test
    void findByIdReturnsApplicationWhenItExists() {
        JobApplication application = sampleApplication();
        when(repository.findById(1L)).thenReturn(Optional.of(application));

        assertThat(service.findById(1L)).isSameAs(application);
    }

    @Test
    void findByIdThrowsWhenApplicationIsMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(JobApplicationNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createSavesThroughRepository() {
        JobApplication application = sampleApplication();
        when(repository.save(application)).thenReturn(application);

        assertThat(service.create(application)).isSameAs(application);
        verify(repository).save(application);
    }

    @Test
    void updateCopiesChangesOntoExistingApplication() {
        JobApplication existing = sampleApplication();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        JobApplication changes = new JobApplication("Acme", "Graduate Engineer", ApplicationStatus.INTERVIEWING, LocalDate.of(2026, 9, 28), "Interview booked");

        JobApplication result = service.update(1L, changes);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.INTERVIEWING);
        assertThat(result.getNotes()).isEqualTo("Interview booked");
        verify(repository, never()).save(any());
    }

    @Test
    void deleteThrowsAndDeletesNothingWhenApplicationIsMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(JobApplicationNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}