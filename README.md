# Case Task Service API

Backend API for managing caseworker tasks.

This service allows caseworkers to create, view, update, and delete tasks. It is implemented as a Kotlin Spring Boot REST API with PostgreSQL persistence, Flyway migrations, validation, error handling, and OpenAPI documentation.

## Features

- Create a task
- Retrieve a task by ID
- Retrieve all tasks
- Update task details
- Update task status
- Delete a task
- Store tasks in PostgreSQL
- Validate incoming requests
- Return consistent JSON error responses
- Expose OpenAPI/Swagger documentation
- Include unit, integration, controller, and documentation smoke tests

## Technology Stack

- Kotlin
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Gradle
- JUnit 5
- MockMvc
- H2 for test profile
- OpenAPI/Swagger via springdoc-openapi
- Docker Compose for local PostgreSQL

## Requirements

To run this project locally, you need:

- Java 21
- Docker and Docker Compose
- Git

The project uses the Gradle wrapper, so Gradle does not need to be installed globally.

## Running the Application Locally

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Check PostgreSQL is healthy:

```bash
docker compose ps
```

Run the application:

```bash
./gradlew bootRun
```

The API will run on:

```text
http://localhost:4000
```

Check the health endpoint:

```bash
curl -i http://localhost:4000/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## Local Database Configuration

The local PostgreSQL container is exposed on host port `5433`:

```text
localhost:5433
```

This avoids conflicts with local PostgreSQL installations that may already use port `5432`.

Default local database values:

```text
Database: case_task_service
Username: case_task_service
Password: case_task_service
```

Environment variables can override these values:

```text
CASE_TASK_DB_URL
CASE_TASK_DB_USERNAME
CASE_TASK_DB_PASSWORD
SERVER_PORT
```

Example:

```bash
CASE_TASK_DB_URL=jdbc:postgresql://localhost:5433/case_task_service \
CASE_TASK_DB_USERNAME=case_task_service \
CASE_TASK_DB_PASSWORD=case_task_service \
./gradlew bootRun
```

## Running Tests

Run the full build:

```bash
./gradlew clean build
```

Run tests only:

```bash
./gradlew test
```

Generate test coverage report:

```bash
./gradlew jacocoTestReport
```

Coverage report location:

```text
build/reports/jacoco/test/html/index.html
```

## API Documentation

Swagger UI is available at:

```text
http://localhost:4000/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
http://localhost:4000/v3/api-docs
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/tasks` | Create a task |
| GET | `/tasks` | Retrieve all tasks |
| GET | `/tasks/{taskId}` | Retrieve a task by ID |
| PUT | `/tasks/{taskId}` | Update task details |
| PATCH | `/tasks/{taskId}/status` | Update task status |
| DELETE | `/tasks/{taskId}` | Delete a task |

## Task Model

A task contains:

```json
{
  "id": "3e0cc629-cc55-406b-ada6-f33bfbdf92b0",
  "title": "Prepare case bundle",
  "description": "Collect required documents",
  "status": "TODO",
  "dueDateTime": "2026-06-12T16:30:00Z",
  "createdAt": "2026-05-14T09:30:00Z",
  "updatedAt": "2026-05-14T09:30:00Z"
}
```

Allowed status values:

```text
TODO
IN_PROGRESS
COMPLETED
```

## Curl Examples

### Create a task

```bash
curl -i -X POST http://localhost:4000/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Prepare case bundle",
    "description": "Collect required documents",
    "dueDateTime": "2026-06-12T16:30:00Z"
  }'
```

Expected response:

```text
HTTP/1.1 201
Location: /tasks/{taskId}
```

### Retrieve all tasks

```bash
curl -i http://localhost:4000/tasks
```

Expected response:

```text
HTTP/1.1 200
```

### Retrieve a task by ID

Replace the ID with a real task ID returned from the create request.

```bash
curl -i http://localhost:4000/tasks/3e0cc629-cc55-406b-ada6-f33bfbdf92b0
```

Expected response:

```text
HTTP/1.1 200
```

### Update task details

```bash
curl -i -X PUT http://localhost:4000/tasks/3e0cc629-cc55-406b-ada6-f33bfbdf92b0 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated case bundle",
    "description": "Updated description",
    "dueDateTime": "2026-07-01T10:00:00Z"
  }'
```

Expected response:

```text
HTTP/1.1 200
```

### Update task status

```bash
curl -i -X PATCH http://localhost:4000/tasks/3e0cc629-cc55-406b-ada6-f33bfbdf92b0/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS"
  }'
```

Expected response:

```text
HTTP/1.1 200
```

### Delete a task

```bash
curl -i -X DELETE http://localhost:4000/tasks/3e0cc629-cc55-406b-ada6-f33bfbdf92b0
```

Expected response:

```text
HTTP/1.1 204
```

## Error Responses

The API returns consistent JSON error responses.

Example validation error:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/tasks",
  "timestamp": "2026-05-14T09:30:00Z",
  "fieldErrors": [
    {
      "field": "title",
      "message": "Title must not be blank"
    }
  ]
}
```

Example not found error:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task with id '99999999-9999-9999-9999-999999999999' was not found",
  "path": "/tasks/99999999-9999-9999-9999-999999999999",
  "timestamp": "2026-05-14T09:30:00Z"
}
```

## Validation Rules

| Field | Rule |
|---|---|
| `title` | Required, must not be blank, maximum 120 characters |
| `description` | Optional, maximum 1000 characters |
| `status` | Must be one of `TODO`, `IN_PROGRESS`, `COMPLETED` |
| `dueDateTime` | Required ISO-8601 date/time |

Due dates in the past are allowed. This supports use cases where caseworkers may need to record overdue or migrated tasks.

## Architecture

The service follows a layered, ports-and-adapters style structure:

```text
task/domain
task/application
task/application/port
task/adapter/in/web
task/adapter/out/persistence
```

### Domain Layer

Contains the core task model and business rules:

- `Task`
- `TaskId`
- `TaskStatus`
- `TaskFactory`
- `DefaultTaskFactory`
- `TaskRules`

The domain layer is framework-light and does not depend on HTTP or database concerns.

### Application Layer

Coordinates use cases through the `TaskFacade`:

- Create task
- Retrieve task
- Retrieve all tasks
- Update task details
- Update task status
- Delete task

The facade hides internal orchestration from the controller.

### Persistence Adapter

The persistence layer implements the `TaskRepository` port using Spring Data JPA.

The domain model is kept separate from the JPA entity. Mapping is handled by `TaskPersistenceMapper`.

### Web Adapter

The web adapter exposes REST endpoints through `TaskController`.

Request/response conversion is handled by `TaskWebMapper`.

## Design Decisions

### Kotlin on Spring Boot

Kotlin was chosen for productivity and type-safety while staying close to the Java/Spring ecosystem.

### PostgreSQL and Flyway

PostgreSQL is used for persistent storage. Flyway manages database schema changes through versioned migrations.

Hibernate is configured with:

```text
ddl-auto: validate
```

This means Hibernate validates the schema but does not create or modify tables automatically.

### Factory Pattern

`TaskFactory` centralises task creation.

It ensures new tasks are created with:

- Generated task ID
- Default `TODO` status
- Controlled timestamps
- Normalised title and description

### Facade Pattern

`TaskFacade` provides a clean entry point for task use cases.

The REST controller depends on the facade rather than coordinating repositories, factories, and domain logic directly.

### Error Handling

`GlobalExceptionHandler` centralises API error handling and prevents internal stack traces from being exposed to API clients.

### Test Profile

Tests use H2 in PostgreSQL compatibility mode to keep the build self-contained and easy to run.

The runtime application uses PostgreSQL.

## Testing Strategy

The project includes:

| Test Type | Purpose |
|---|---|
| Domain tests | Validate task creation, updates, and business rules |
| Mapper tests | Validate conversion between domain and persistence models |
| Persistence tests | Validate save, find, list, exists, and delete behaviour |
| Facade tests | Validate application use-case orchestration |
| Controller tests | Validate REST endpoint behaviour |
| Error handling tests | Validate consistent API error responses |
| OpenAPI tests | Validate API documentation is exposed |

## Assumptions

- Authentication and authorisation are outside the scope of this task.
- Caseworker assignment is outside the current scope.
- A task can be overdue.
- Updating status is separate from updating task details.
- Task deletion returns `404 Not Found` when the task does not exist.
- Task list ordering is deterministic: due date/time ascending, then creation time ascending.

## Future Improvements

Given more time, the following could be added:

- Authentication and role-based access control
- Pagination and filtering for task lists
- Audit history for task changes
- Assigned caseworker field
- Priority field
- Search by title or description
- Testcontainers-based PostgreSQL integration tests
- Structured logging and correlation IDs
- CI pipeline with automated build and test execution
- Deployment configuration for a container platform

## Final Verification Commands

Before submission, run:

```bash
./gradlew clean build
docker compose up -d postgres
./gradlew bootRun
```

Then verify:

```bash
curl -i http://localhost:4000/health
curl -i http://localhost:4000/v3/api-docs
```

Swagger UI:

```text
http://localhost:4000/swagger-ui/index.html
```

## Name-Blind Submission Note

This repository has been prepared for name-blind review.

Before submitting, check that the repository does not contain:

- Candidate real name
- Personal email address
- Company name
- Personal website
- YouTube channel
- Screenshots showing local machine paths
- Build reports containing local file paths
- Personal Git commit author details

Recommended local Git identity for this repository:

```bash
git config user.name "Candidate"
git config user.email "candidate@example.invalid"
```