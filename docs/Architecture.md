# EcoSort 2.0 — Architecture

> Status: ACCEPTED BASELINE

## 1. Purpose

EcoSort 2.0 is a real working waste-identification and waste-management application. It is being rebuilt as a maintainable, testable system rather than as a Lovable-generated prototype.

The core user flow is:

```text
User
  ↓
React Web App
  ↓
Spring Boot API
  ↓
Application Services
  ↓
Domain + Persistence / AI / Recommendation Boundaries
  ↓
MongoDB + AI Inference Service
```

The previous EcoSort implementation is reference material only. Its code, Supabase architecture, and implementation assumptions are not part of the new system.

## 2. Technology Stack

- Backend: Java + Spring Boot
- Build: Maven
- Database: MongoDB / MongoDB Atlas
- Frontend: React + TypeScript
- AI/ML: isolated behind an explicit inference interface; provider/model may be selected during implementation without changing the application contract
- Backend testing: JUnit 5, Mockito, Spring Boot Test, and appropriate integration-test tooling
- Frontend testing: project-appropriate TypeScript/React test tooling
- Version control: Git + GitHub

No technology may be changed casually after a module contract depends on it. A change requires an explicit architecture decision and regression verification.

## 3. Backend Architecture

The backend follows a layered architecture:

```text
Controller / API
       ↓
Application / Service
       ↓
Domain
       ↓
Repository / External-Service Interfaces
       ↓
MongoDB / AI / Other External Systems
```

### Controller layer

- HTTP request/response handling
- DTO validation
- Authentication context extraction where applicable
- No business logic or direct database access

### Application / Service layer

- Use-case orchestration
- Business rules
- Coordination between domain, repositories, AI, and recommendation services
- Transaction/boundary management where applicable

### Domain layer

- Core entities/value objects
- Domain rules
- Stable concepts shared across modules
- No dependency on HTTP or framework-specific infrastructure unless explicitly justified

### Repository / Integration layer

- MongoDB persistence
- External AI inference
- Other external service clients
- Infrastructure-specific concerns isolated behind interfaces

## 4. Frontend Architecture

The frontend is a React + TypeScript application organized around features and shared infrastructure.

```text
Pages / Feature Components
          ↓
API Client / State Management
          ↓
Spring Boot REST API
```

The frontend must consume the accepted backend API contracts. It must not duplicate backend business rules or access MongoDB directly.

## 5. Core Functional Scope

The initial system scope is:

1. User authentication and protected application access.
2. Waste image/input submission.
3. AI-assisted waste classification.
4. Classification result with confidence/error handling.
5. Disposal/recycling recommendation based on the classification.
6. Persistent user waste history.
7. Reports and dashboard analytics based on stored activity.
8. Responsive web UI integrating the complete flow.
9. A civic-reporting handoff that lets users open the official Swachhata platform for sanitation/waste complaints without EcoSort collecting location or directly submitting the complaint.
10. Production-oriented configuration, security, testing, and release readiness.

Features outside this scope are not to be added speculatively.

## 6. Module Sequence

The project is divided into ten modules. The repository contains the complete module skeleton from the beginning, but implementation happens strictly one module at a time.

| Module | Name | Primary Responsibility |
|---|---|---|
| M1 | Backend Foundation | Spring Boot project foundation, configuration, health baseline, common infrastructure conventions |
| M2 | Identity & Access | User model, authentication, authorization, protected API boundary |
| M3 | Waste Domain & Persistence | Waste entities, classification records, MongoDB repositories, persistence contracts |
| M4 | Classification Application | Classification use case, validation, result model, stable AI inference boundary |
| M5 | AI Inference Integration | Concrete AI/model provider integration behind the M4 contract |
| M6 | Disposal Recommendations | Disposal/recycling rules and recommendation service tied to classifications |
| M7 | History & Reports | User history, filtering, report-oriented APIs and aggregation |
| M8 | Dashboard & Analytics | Metrics and dashboard data derived from verified application data |
| M9 | Web Application Integration | React UI, authentication flow, identification flow, history, reports, dashboard, and civic-reporting handoff |
| M10 | Production Readiness | End-to-end verification, security hardening, configuration, observability, final regression, and reproducible local release readiness |

Later modules may consume earlier modules, but may not silently redefine their verified contracts.

## 7. Data Flow

Primary classification flow:

```text
User uploads/submits waste input
        ↓
Frontend validates basic request
        ↓
Backend Controller validates request
        ↓
Classification Service
        ↓
AI Inference Interface
        ↓
Concrete AI Provider
        ↓
Classification Result
        ↓
Validation / normalization
        ↓
Waste Record persisted to MongoDB
        ↓
Disposal Recommendation Service
        ↓
API response
        ↓
Frontend result display
```

Civic-reporting flow:

```text
User identifies a sanitation/waste issue
        ↓
EcoSort provides context/instructions
        ↓
User selects "Report on Swachhata"
        ↓
External official Swachhata platform
        ↓
User completes the complaint there
```

EcoSort does not collect the user's location for this handoff, does not submit the complaint on the user's behalf, and does not store Swachhata complaint data. The external platform owns complaint routing and status handling.

History and analytics consume persisted records rather than calling the AI provider again.

## 8. AI Boundary

AI is an implementation component, not the application architecture itself.

The application owns a stable classification contract containing, as applicable:

- Input/reference to the waste item or image
- Predicted category
- Confidence
- Provider/model metadata when useful
- Failure state/error information

The concrete model/provider is isolated behind an interface/client boundary. Changing the provider must not require controllers, persistence, or frontend code to know provider-specific details.

Production code must never fabricate successful AI predictions as a substitute for unavailable inference.

## 9. Persistence Architecture

MongoDB is the primary persistence system.

Persistence access is isolated through repository abstractions. Domain/application services must not scatter MongoDB queries throughout business logic.

The exact document schema and indexes are finalized in the relevant module contract before implementation.

Tests must not accidentally write to a production database or depend on developer-specific credentials.

For local/self-hosted use, MongoDB configuration is externalized so each installation can provide its own MongoDB or MongoDB Atlas resources. A shared project database is not a required runtime dependency.

## 10. API Contract

The backend exposes a versionable REST API. Exact endpoints and DTO schemas are recorded in `docs/module-contracts.md` as they become accepted.

API rules:

- Validate external input.
- Use explicit request/response DTOs where appropriate.
- Return consistent error responses.
- Keep controllers thin.
- Never expose persistence internals unnecessarily.
- Never expose credentials or secrets.
- Preserve verified API contracts across later modules.

## 11. Security Baseline

- Secrets and credentials remain outside Git.
- Configuration is externalized.
- Authentication and authorization are enforced at protected boundaries.
- User-owned data must not be exposed across users.
- Uploaded/input data is validated.
- External AI/API responses are treated as untrusted input.
- Production configuration must not rely on committed secrets.
- EcoSort must not request or store location data solely to redirect a user to Swachhata.

## 12. Testing Architecture

Every module has focused tests under its module directory and contributes to the main application test suite.

Verification has two levels:

1. Focused module tests covering the new behavior and contract.
2. Full regression tests proving earlier verified behavior still works.

No module is considered `VERIFIED` based only on compilation or application startup.

## 13. Repository Structure

The intended high-level structure is:

```text
EcoSortWebDevProject-101/
├── backend/
├── frontend/
├── modules/
│   ├── module-01/
│   │   ├── README.md
│   │   └── tests/
│   ├── module-02/
│   │   ├── README.md
│   │   └── tests/
│   ├── ...
│   └── module-10/
│       ├── README.md
│       └── tests/
├── docs/
│   ├── Architecture.md
│   ├── module-contracts.md
│   └── PROGRESS.md
└── README.md
```

The module folders document ownership and verification. Production Java/TypeScript source remains in the normal backend/frontend source trees.

## 14. Architectural Decisions

### ADR-001 — Java/Spring Boot backend

Status: Accepted.

The backend will use Java with Spring Boot.

### ADR-002 — MongoDB

Status: Accepted.

MongoDB will be the primary application database.

### ADR-003 — React + TypeScript frontend

Status: Accepted.

The web client will use React and TypeScript.

### ADR-004 — Clean rebuild

Status: Accepted.

The old Lovable/Supabase EcoSort project is reference-only. EcoSort 2.0 is independently structured and implemented.

### ADR-005 — Fixed module workflow

Status: Accepted.

All ten module skeletons are established up front. Implementation proceeds one module at a time with a module README, focused tests, regression testing, diff review, documentation update, and checkpoint before moving forward.

### ADR-006 — Swachhata civic-reporting handoff

Status: Accepted.

EcoSort will provide a user-facing handoff to the official Swachhata platform for sanitation and waste-management complaints. EcoSort will not directly integrate complaint submission, collect the user's location for this purpose, or store external complaint data. The user completes the complaint on Swachhata, which owns the municipal routing and complaint lifecycle.

### ADR-007 — Local/self-hosted runtime

Status: Accepted.

EcoSort is designed to be runnable from a fresh GitHub clone using locally supplied or user-owned external resources. Public deployment is not required for the core project or portfolio demonstration. Users may run the frontend, backend, MongoDB, and AI integration using their own resources and configuration. A public deployment may be added later without changing the core module contracts.

## 15. Constraints

- Do not implement future modules early.
- Do not bypass module contracts for convenience.
- Do not introduce unrelated refactors during a module.
- Do not replace required working functionality with mocks or hard-coded results.
- Do not commit secrets.
- Do not weaken or delete existing tests to make a module pass.
- Do not report a module as verified until its required checks pass.
- Do not turn the Swachhata handoff into a government API integration unless a separate architectural decision explicitly approves it.
- Do not make a shared project-owned MongoDB instance a required runtime dependency for users cloning the repository.
- Public deployment is optional and must not become a hidden acceptance criterion for module completion.

## 16. Open Decisions

The following implementation-level decisions are intentionally resolved when their module is reached and must be recorded in the canonical docs:

- Exact authentication mechanism and provider
- Exact AI model/provider and inference deployment
- Final MongoDB document fields and indexes
- Exact REST endpoint paths and DTOs
- Final frontend state-management details
- Observability implementation details
- Optional public deployment platform and topology, if deployment is later desired

These decisions must not be invented silently by an implementation AI. They must be recorded when accepted.
