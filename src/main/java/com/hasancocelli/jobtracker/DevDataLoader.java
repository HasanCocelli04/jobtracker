package com.hasancocelli.jobtracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DevDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    private final JobApplicationService service;

    public DevDataLoader(JobApplicationService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.create(new JobApplication("Acme Consulting", "Graduate Software Engineer", ApplicationStatus.APPLIED, LocalDate.of(2026, 9, 28), "Applied via LinkedIn"));
        service.create(new JobApplication("Example Ltd", "Junior Java Developer", ApplicationStatus.INTERVIEWING, LocalDate.of(2026, 9, 21), "First interview booked"));

        log.info("Loaded {} job applications", service.findAll().size());
    }
}