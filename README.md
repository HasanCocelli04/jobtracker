# Job Tracker API
![CI](https://github.com/HasanCocelli04/jobtracker/actions/workflows/ci.yml/badge.svg)

A REST API for tracking job applications, built with Spring Boot and Java 21. Deployed on Render with a PostgreSQL database hosted on Neon.

**Live:** https://jobtracker-rpx9.onrender.com/api/applications
**Interactive API docs:** https://jobtracker-rpx9.onrender.com/swagger-ui.html

Note: it runs on Render's free tier, so the first request after a period of inactivity can take up to a minute while the service wakes up.

## Tech stack

Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation), Hibernate, PostgreSQL, H2, JUnit 5, Mockito, MockMvc, Gradle, Docker

## Endpoints

| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/api/applications` | List all applications | 200 |
| GET | `/api/applications/{id}` | Get one application | 200 (404 if missing) |
| POST | `/api/applications` | Create an application | 201 with Location header |
| PUT | `/api/applications/{id}` | Update an application | 200 |
| DELETE | `/api/applications/{id}` | Delete an application | 204 |

Status is one of `APPLIED`, `INTERVIEWING`, `OFFER`, `REJECTED`.

## Example

```
curl -i -X POST https://jobtracker-rpx9.onrender.com/api/applications \
  -H "Content-Type: application/json" \
  -d '{"company":"Test Co","role":"Backend Developer","status":"APPLIED","dateApplied":"2026-10-05","notes":"Applied via LinkedIn"}'
```

Invalid input returns 400 with field level errors in RFC 9457 Problem Details format:

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "company": "Company is required",
    "status": "Status is required"
  }
}
```

## Design

- Layered structure: controller (HTTP), service (business logic and transactions), repository (Spring Data JPA)
- Constructor injection throughout, which keeps the service unit testable with Mockito
- Bean Validation on request bodies, with errors handled centrally in a `@RestControllerAdvice`
- Three profiles: default (H2 with sample data), test (H2, no sample data) and prod (PostgreSQL)
- Database credentials are supplied through environment variables and never committed
- Multi stage Docker build so the runtime image contains only the JRE and the packaged jar

## Tests

```
./gradlew test
```

13 tests: unit tests for the service layer using Mockito, and integration tests that send real HTTP requests through MockMvc against an in-memory database, covering success cases, 404s and validation errors.

## Running locally

```
./gradlew bootRun
```

Then open http://localhost:8080/api/applications. Locally it uses H2 and loads two sample applications.

## Known limitations

- No authentication, so the live demo data can be changed by anyone
- The schema is managed by Hibernate (`ddl-auto=update`); a migration tool such as Flyway would be the next step
- No pagination on the list endpoint