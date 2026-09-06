# EcoSort 2.0 — Architecture

> Status: DESIGN PHASE

## 1. Purpose

EcoSort 2.0 is a real working waste-identification and waste-management application. The system will be designed as a maintainable, testable application rather than a Lovable-generated prototype.

## 2. Technology Direction

The current technology direction is:

- Backend: Java + Spring Boot
- Database: MongoDB
- Frontend: React + TypeScript
- AI/ML: isolated behind a defined inference boundary
- Build: Maven unless a later architecture decision changes this
- Testing: JUnit / Spring Boot testing stack

These choices are architectural direction, not permission to implement future modules prematurely.

## 3. Architecture

Detailed component architecture will be finalized before Module 1 implementation.

Expected backend layering:

```text
API / Controller
      ↓
Application / Service
      ↓
Domain
      ↓
Repository / External Service Boundaries
      ↓
MongoDB / AI / Other External Systems
```

The exact package structure, domain model, API surface, authentication model, AI integration method, and deployment architecture must be recorded here before the corresponding modules are implemented.

## 4. Module Sequence

The module sequence is intentionally TBD until the functional scope and system boundaries are finalized.

No implementation AI should invent or implement future modules without an accepted module contract.

## 5. Architectural Constraints

- Real functionality over mock functionality.
- Secrets must never be committed.
- Module boundaries must be explicit.
- Later modules must preserve verified earlier contracts.
- External AI inference must be isolated behind a stable boundary.
- Database access must not be scattered through business logic.
- Tests are part of the implementation contract.

## 6. Decisions

### ADR-001 — Java/Spring Boot backend

Status: Accepted direction.

EcoSort 2.0 will use Java and Spring Boot for backend development.

### ADR-002 — MongoDB

Status: Accepted direction.

MongoDB will be the primary application database unless a later explicit architectural decision changes this.

### ADR-003 — Existing EcoSort prototype

Status: Reference only.

The previous Lovable/Supabase EcoSort implementation is treated as a product/reference source, not as the codebase for EcoSort 2.0. The new project must be independently structured and implemented.

## 7. Risks / Open Questions

- Final feature scope
- Authentication requirements
- Waste classification model/provider
- AI inference deployment strategy
- Exact MongoDB document model
- Frontend/backend API contract
- Deployment target
- Observability requirements

These must be resolved before the modules depending on them are implemented.
