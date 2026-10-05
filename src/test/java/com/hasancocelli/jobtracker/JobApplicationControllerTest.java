package com.hasancocelli.jobtracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobApplicationRepository repository;

    private JobApplication saveSample() {
        return repository.save(new JobApplication("Acme", "Graduate Engineer", ApplicationStatus.APPLIED, LocalDate.of(2026, 9, 28), "Applied online"));
    }

    @Test
    void createReturns201WithLocationHeader() throws Exception {
        String json = """
                {"company":"Test Co","role":"Backend Developer","status":"APPLIED","dateApplied":"2026-09-28","notes":"Applied online"}
                """;

        mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.company").value("Test Co"));
    }

    @Test
    void getByIdReturnsSavedApplication() throws Exception {
        JobApplication saved = saveSample();

        mockMvc.perform(get("/api/applications/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Acme"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void getMissingApplicationReturns404() throws Exception {
        mockMvc.perform(get("/api/applications/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Job application not found with id 999999"));
    }

    @Test
    void createWithBlankCompanyAndNoStatusReturns400WithFieldErrors() throws Exception {
        String json = """
                {"company":"","role":"Backend Developer","dateApplied":"2026-09-28"}
                """;

        mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.company").value("Company is required"))
                .andExpect(jsonPath("$.errors.status").value("Status is required"));
    }

    @Test
    void createWithFutureDateReturns400() throws Exception {
        String json = """
                {"company":"Test Co","role":"Backend Developer","status":"APPLIED","dateApplied":"2099-01-01"}
                """;

        mockMvc.perform(post("/api/applications").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dateApplied").value("Date applied cannot be in the future"));
    }

    @Test
    void updateChangesStatus() throws Exception {
        JobApplication saved = saveSample();
        String json = """
                {"company":"Acme","role":"Graduate Engineer","status":"INTERVIEWING","dateApplied":"2026-09-28","notes":"Interview booked"}
                """;

        mockMvc.perform(put("/api/applications/" + saved.getId()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEWING"))
                .andExpect(jsonPath("$.notes").value("Interview booked"));
    }

    @Test
    void deleteReturns204AndApplicationIsGone() throws Exception {
        JobApplication saved = saveSample();

        mockMvc.perform(delete("/api/applications/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/applications/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}