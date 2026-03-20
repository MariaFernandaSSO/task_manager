# Task Manager API

> Language: English | [Português (Brasil)](README.pt-BR.md)

A clean and secure REST API for task management, built with Spring Boot.

## Highlights

- JWT-based authentication (`/auth/register`, `/auth/login`)
- Full task workflow (`/tasks`) for authenticated users
- Soft delete support for tasks (`is_deleted`)
- Database versioning with Liquibase
- Interactive API docs with Swagger (dev profile)
- Unit and integration tests with JUnit, Mockito, and Testcontainers

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL
- Liquibase
- JWT (`jjwt`)
- springdoc-openapi (Swagger UI)
- Docker + Docker Compose

## Project Structure

```text
src/main/java/org/maria/taskmanager
  config/         # Security, JWT, Swagger configs
  controller/     # HTTP endpoints
  service/        # Business rules
  repository/     # Data access
  model/          # JPA entities
  dto/            # Request/response payloads
  mapper/         # Mapping layer
  security/       # JWT filter/util
  exception/      # Global error handling
```

## Requirements

- JDK 17+
- Maven 3.9+ (or use `./mvnw`)
- Docker + Docker Compose (optional, recommended)
- PostgreSQL (if running without Docker)

## Environment Variables

This project reads env vars from shell and from `.env/.env` when using Docker Compose.

| Variable | Default (dev) | Description |
|---|---|---|
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | `5432` | Database port |
| `DB_NAME` | `taskmanager` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (fallback in `application.yml`) | JWT signing key |
| `SERVER_PORT` | `8080` | Application HTTP port |

Example `.env/.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskmanager
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=change-me-in-real-environments
SERVER_PORT=8080
```

## Running Locally (without Docker)

1. Ensure PostgreSQL is running.
2. Set env vars (or rely on dev defaults).
3. Start the application.

```bash
cd /d/projetos_avulsos/taskManager
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Running with Docker Compose

This starts both Postgres and the app container.

```bash
cd /d/projetos_avulsos/taskManager
docker compose up --build
```

Stop and remove containers:

```bash
cd /d/projetos_avulsos/taskManager
docker compose down
```

## API Docs (Swagger)

When running with `dev` profile:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Note: in `prod` profile, Swagger is disabled by config.

## Auth Flow

### Register

`POST /auth/register`

```json
{
  "name": "Maria",
  "email": "maria@email.com",
  "password": "senha123"
}
```

### Login

`POST /auth/login`

```json
{
  "email": "maria@email.com",
  "password": "senha123"
}
```

Use the returned JWT in protected endpoints:

```text
Authorization: Bearer <token>
```

## Main Endpoints

- `POST /auth/register` - register user
- `POST /auth/login` - authenticate user
- `POST /tasks` - create task
- `GET /tasks` - list tasks (supports `page`, `pageSize`, `status`)
- `GET /tasks/{id}` - get task by id
- `PATCH /tasks/{id}` - partially update task
- `DELETE /tasks/{id}` - soft delete task

## Running Tests

Run all tests:

```bash
cd /d/projetos_avulsos/taskManager
./mvnw test
```

Run only service tests:

```bash
cd /d/projetos_avulsos/taskManager
./mvnw -Dtest=AuthServiceTest,TaskServiceTest test
```

## Troubleshooting

- `Failed to determine suitable jdbc url`
  - Ensure DB env vars are set or run with `dev` profile defaults.
- `missing column [is_deleted] in table [tasks]`
  - Ensure Liquibase is enabled and latest migrations are applied.
- `Failed to load remote configuration` in Swagger UI
  - Confirm app is running and open `http://localhost:8080/v3/api-docs` directly.
- Docker warns about missing `DB_*` variables
  - Verify `.env/.env` exists and keys are correctly defined.

## License

This repository includes a `LICENSE` file.
