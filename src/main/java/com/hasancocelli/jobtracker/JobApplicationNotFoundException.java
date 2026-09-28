package com.hasancocelli.jobtracker;

public class JobApplicationNotFoundException extends RuntimeException {

    public JobApplicationNotFoundException(Long id) {
        super("Job application not found with id " + id);
    }
}