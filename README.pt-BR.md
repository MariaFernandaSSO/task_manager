# Task Manager API

> Idioma: Português (Brasil) | [English](README.md)

API REST para gerenciamento de tarefas, segura e organizada, construída com Spring Boot.

## Destaques

- Autenticação com JWT (`/auth/register`, `/auth/login`)
- Fluxo completo de tarefas (`/tasks`) para usuários autenticados
- Suporte a soft delete em tarefas (`is_deleted`)
- Versionamento de banco com Liquibase
- Documentação interativa com Swagger (perfil `dev`)
- Testes unitários e de integração com JUnit, Mockito e Testcontainers

## Stack Tecnológica

- Java 17
- Spring Boot 3.5.x
- Spring Web, Spring Security, Spring Data JPA
- PostgreSQL
- Liquibase
- JWT (`jjwt`)
- springdoc-openapi (Swagger UI)
- Docker + Docker Compose

## Estrutura do Projeto

```text
src/main/java/org/maria/taskmanager
  config/         # Configurações de segurança, JWT e Swagger
  controller/     # Endpoints HTTP
  service/        # Regras de negócio
  repository/     # Acesso a dados
  model/          # Entidades JPA
  dto/            # Payloads de request/response
  mapper/         # Camada de mapeamento
  security/       # Filtro e utilitário JWT
  exception/      # Tratamento global de erros
```

## Requisitos

- JDK 17+
- Maven 3.9+ (ou usar `./mvnw`)
- Docker + Docker Compose (opcional, recomendado)
- PostgreSQL (se rodar sem Docker)

## Variáveis de Ambiente

O projeto lê variáveis do shell e de `.env/.env` quando executado com Docker Compose.

| Variável | Padrão (dev) | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do banco |
| `DB_PORT` | `5432` | Porta do banco |
| `DB_NAME` | `taskmanager` | Nome do banco |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `JWT_SECRET` | (fallback em `application.yml`) | Chave de assinatura JWT |
| `SERVER_PORT` | `8080` | Porta HTTP da aplicação |

Exemplo de `.env/.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskmanager
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=change-me-in-real-environments
SERVER_PORT=8080
```

## Executando Localmente (sem Docker)

1. Garanta que o PostgreSQL está ativo.
2. Configure as variáveis de ambiente (ou use os padrões de `dev`).
3. Inicie a aplicação.

```bash
cd /d/projetos_avulsos/taskManager
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Executando com Docker Compose

Este comando sobe Postgres e aplicação.

```bash
cd /d/projetos_avulsos/taskManager
docker compose up --build
```

Para parar e remover os containers:

```bash
cd /d/projetos_avulsos/taskManager
docker compose down
```

## Documentação da API (Swagger)

Com o perfil `dev` ativo:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Observação: no perfil `prod`, o Swagger está desabilitado por configuração.

## Fluxo de Autenticação

### Registro

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

Use o JWT retornado nos endpoints protegidos:

```text
Authorization: Bearer <token>
```

## Principais Endpoints

- `POST /auth/register` - registra usuário
- `POST /auth/login` - autentica usuário
- `POST /tasks` - cria tarefa
- `GET /tasks` - lista tarefas (suporta `page`, `pageSize`, `status`)
- `GET /tasks/{id}` - busca tarefa por id
- `PATCH /tasks/{id}` - atualiza tarefa parcialmente
- `DELETE /tasks/{id}` - aplica soft delete

## Executando Testes

Rodar todos os testes:

```bash
cd /d/projetos_avulsos/taskManager
./mvnw test
```

Rodar apenas testes de serviço:

```bash
cd /d/projetos_avulsos/taskManager
./mvnw -Dtest=AuthServiceTest,TaskServiceTest test
```

## Troubleshooting

- `Failed to determine suitable jdbc url`
  - Verifique as variáveis de banco ou rode com os padrões do perfil `dev`.
- `missing column [is_deleted] in table [tasks]`
  - Verifique se o Liquibase está habilitado e com migrations atualizadas.
- `Failed to load remote configuration` no Swagger UI
  - Confirme se a aplicação está no ar e abra `http://localhost:8080/v3/api-docs` diretamente.
- Docker alerta variáveis `DB_*` ausentes
  - Verifique se `.env/.env` existe e contém as chaves corretas.

## Licença

Este repositório possui um arquivo `LICENSE`.

