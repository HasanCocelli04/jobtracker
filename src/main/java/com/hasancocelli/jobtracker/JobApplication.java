package com.hasancocelli.jobtracker;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;

    private String role;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate dateApplied;

    private String notes;

    protected JobApplication() {
    }

    public JobApplication(String company, String role, ApplicationStatus status, LocalDate dateApplied, String notes) {
        this.company = company;
        this.role = role;
        this.status = status;
        this.dateApplied = dateApplied;
        this.notes = notes;
    }
}