# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Control-Booth is an experimental Internal Developer Platform (IDP) — similar to Backstage — for exposing services and information to backend developers. It is explicitly incomplete and under active exploration.

## Commands

### Backend (Spring Boot / Maven)
```bash
./scripts/app-get-assets.sh   # Download frontend assets before first build
mvn clean package          # Build the project
mvn spring-boot:run        # Run locally (uses 'local' Spring profile)
mvn spring-boot:build-image   # Build Docker image
```

### Frontend (React, port 3030)
```bash
cd app
npm install       # Install dependencies
npm run start     # Start dev server (proxies API to localhost:8080)
npm run build     # Build production bundle
npm test          # Run tests
```

### Infrastructure
```bash
docker-compose up -d   # Start PostgreSQL (5433), Keycloak (9000), OpenLDAP (389), phpLDAPAdmin (8081)
```

## Architecture

The app is a two-tier system: a Spring Boot API backend and a React SPA frontend, backed by PostgreSQL and integrated with Keycloak (OAuth2/OIDC) and OpenLDAP (directory source).

**Port map:** Frontend 3030 → Backend 8080 → PostgreSQL 5433. Keycloak at 9000, LDAP at 389.

### Backend (`src/main/java/com/pehrs/controlbooth/`)

Standard layered architecture:
- **Catalog providers** (`catalog/`) — scheduled jobs that populate the DB. `LdapCatalogEntityProvider` scans LDAP every 5 minutes. `GitlabCatalogEntityProvider` is a stub.
- **Controllers** (`controller/`) — REST endpoints under `/api/`. Main endpoint: `GET /api/entity`.
- **Domain entities** (`domain/catalog/`) — JPA entity hierarchy: abstract `CatalogEntity` → User, Group, System, Component, API, Resource, Domain. Schema is auto-managed by Hibernate (`ddl-auto: update`). Annotations (key-value metadata) stored in `entity_annotation` table.
- **Services** (`service/`) — business logic between controllers and repositories.
- **Config** (`config/`) — OAuth2/CORS in `Oauth2SecurityConfig`, JSON in `JacksonConfig`.

### Frontend (`app/src/`)

React 19 SPA with OAuth2 authentication wrapping the entire app (`ProtectedApp.js`). Uses:
- **react-oidc-context** for Keycloak OIDC integration
- **React Router DOM 7** for client-side routing (routes defined in `navigation.js`)
- **TanStack Query** for server state / API calls
- **Reactstrap + Bootstrap 5** for UI components
- **HTMX + Thymeleaf** also used in the Spring backend for server-rendered views (e.g., About page)

The dev server (`craco.config.js`) proxies API requests to `localhost:8080`.

### Key integration points
- LDAP user photos (JPEG) are fetched from the directory and served by `UserRestController` as binary responses.
- Frontend `config.js` contains the OIDC client configuration pointing at Keycloak.
- `application.yml` (Spring) holds DB, OAuth2 resource server, and LDAP connection config — local overrides live in `application-local.yml` (not committed).
