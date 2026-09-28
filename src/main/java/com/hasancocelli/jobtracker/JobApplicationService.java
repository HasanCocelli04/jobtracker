package com.hasancocelli.jobtracker;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public List<JobApplication> findAll() {
        return repository.findAll();
    }

    public JobApplication findById(Long id) {
        Optional<JobApplication> result = repository.findById(id);
        if (result.isEmpty()) {
            throw new JobApplicationNotFoundException(id);
        }
        return result.get();
    }

    public JobApplication create(JobApplication application) {
        return repository.save(application);
    }

    @Transactional
    public JobApplication update(Long id, JobApplication changes) {
        JobApplication existing = findById(id);
        existing.setCompany(changes.getCompany());
        existing.setRole(changes.getRole());
        existing.setStatus(changes.getStatus());
        existing.setDateApplied(changes.getDateApplied());
        existing.setNotes(changes.getNotes());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        JobApplication existing = findById(id);
        repository.delete(existing);
    }
}