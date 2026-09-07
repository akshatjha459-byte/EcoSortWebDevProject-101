# EcoSort 2.0 — Module Contracts

> Status: ACCEPTED BASELINE

This is the canonical registry of contracts that cross module boundaries. Implementation AIs must read this file before changing a module.

## Contract Rules

1. A verified contract is stable by default.
2. Later modules consume earlier contracts; they do not silently redefine them.
3. Breaking changes require an explicit architecture decision and full regression verification.
4. Private implementation details stay in the module README/source unless another module depends on them.
5. Cross-module APIs, DTOs, domain objects, repository interfaces, AI interfaces, persistence rules, and external-service boundaries belong here.
6. If an implementation discovers a contradiction, stop and report it rather than guessing.

## Module Dependency Direction

```text
M1 Foundation
  ↓
M2 Identity & Access
  ↓
M3 Waste Domain & Persistence
  ↓
M4 Classification Application
  ↓
M5 AI Inference Integration
  ↓
M6 Disposal Recommendations
  ↓
M7 History & Reports
  ↓
M8 Dashboard & Analytics
  ↓
M9 Web Application Integration
  ↓
M10 Production Readiness
```

A module may use stable infrastructure from earlier modules. It must not depend on an unfinished future module.

## Contract Statuses

- `PROPOSED` — identified but not yet implemented/verified.
- `ACCEPTED` — agreed architectural/interface contract.
- `VERIFIED` — exercised by tests and preserved by regression.
- `DEPRECATED` — retained only for compatibility and scheduled for removal.

## M1 — Backend Foundation

**Status:** ACCEPTED

Owns the Spring Boot/Maven foundation, application configuration conventions, environment configuration, health baseline, common error/response conventions where established, and test bootstrap.

**Cross-module contract:** later modules inherit the project/package/build/test conventions and common infrastructure established by M1.

**Must not own:** business-specific waste, authentication, AI, recommendation, reporting, or frontend functionality.

## M2 — Identity & Access

**Status:** ACCEPTED

Owns user identity, authentication, authorization, protected-resource rules, and user identity propagation into application services.

**Cross-module contract:** downstream services can rely on an authenticated user identity and authorization boundary without implementing authentication themselves.

**Compatibility requirement:** user-owned resources must be scoped to the authenticated user.

## M3 — Waste Domain & Persistence

**Status:** VERIFIED

Owns the core waste/classification persistence model and MongoDB repository boundary.

**Cross-module contract:** downstream modules consume domain/application models and repository/service interfaces rather than raw MongoDB queries.

### Domain Model

**WasteStatus** — enum: `PENDING`, `CLASSIFIED`, `FAILED`

**WasteRecord** (immutable domain record):
| Field | Type | Required | Description |
|---|---|---|---|
| `id` | `String` (UUID) | Yes (generated) | Unique record identifier |
| `userId` | `String` | Yes | Owning user ID from authenticated context |
| `inputRef` | `String` | Yes | Reference to the uploaded waste input/image |
| `predictedCategory` | `String` | No (when status=CLASSIFIED) | AI-predicted waste category |
| `confidence` | `Double` | No | AI confidence score 0.0–1.0 |
| `status` | `WasteStatus` | Yes | Current classification state |
| `createdAt` | `Instant` (UTC) | Yes | Record creation timestamp |
| `errorMessage` | `String` | No (when status=FAILED) | Error details for failed classifications |

### MongoDB Document

**Collection:** `waste_records`

Document fields mirror the domain `WasteRecord`. `WasteStatus` is stored as its enum name string.

### Indexes

| Name | Keys | Justification |
|---|---|---|
| `user_created_idx` | `{userId: 1, createdAt: -1}` | User history queries (M7) |
| `user_status_idx` | `{userId: 1, status: 1}` | Filtered queries by status |

### Validation Rules

- `userId` must not be null or blank
- `inputRef` must not be null or blank
- `status` must not be null
- `createdAt` must not be null
- `confidence` if present must be between 0.0 and 1.0
- `predictedCategory` required when `status == CLASSIFIED`

### Repository Methods

`WasteRepository` (domain interface):
- `WasteRecord save(WasteRecord record)` — persists and returns the saved record
- `WasteRecord findById(String id)` — returns null if not found
- `List<WasteRecord> findByUserId(String userId)` — records ordered by `createdAt` descending
- `List<WasteRecord> findByUserIdAndStatus(String userId, WasteStatus status)` — filtered by status, ordered by `createdAt` descending

### Persistence Failure Handling

- Save failures are wrapped in `IllegalStateException` with message "Failed to persist waste record"
- Missing records return `null` (not an exception) from `findById`

### Design Decisions

1. **`inputRef` is a reference, not binary image data.** The actual image is stored elsewhere (e.g., object storage); only a reference string is persisted. This keeps documents small and follows the principle of not storing binary blobs in MongoDB.
2. **`WasteStatus` is stored as a string** in MongoDB rather than an ordinal integer, for readability and backward compatibility.
3. **Timestamps use `Instant`** (UTC) consistent with the M2 `User` model convention.
4. **`findById` returns null** for missing records, matching the existing `UserRepository.findById` convention in M2.
5. **No AI-provider-specific fields** are included; only generic classification result fields that M4 will populate.

## M4 — Classification Application

**Status:** ACCEPTED

Owns the classification use case, input validation, orchestration, classification result contract, and the stable inference interface consumed by the application.

**Cross-module contract:** the application asks for classification through the accepted inference abstraction. Controllers and persistence do not depend on provider-specific AI APIs.

The concrete provider is intentionally owned by M5.

## M5 — AI Inference Integration

**Status:** ACCEPTED

Owns the concrete AI/model/provider client and mapping between provider responses and the M4 inference contract.

**Cross-module contract:** provider-specific details remain behind the M4 interface. Provider failures, timeouts, malformed responses, and invalid predictions are normalized according to the accepted classification error contract.

## M6 — Disposal Recommendations

**Status:** ACCEPTED

Owns disposal/recycling recommendation rules derived from verified classification results.

**Cross-module contract:** recommendation logic consumes classification/domain information and returns a stable recommendation model. It does not perform AI inference or duplicate authentication.

## M7 — History & Reports

**Status:** ACCEPTED

Owns user history retrieval, filtering, report-oriented queries, and required aggregations over persisted waste records.

**Cross-module contract:** history/report APIs use persisted application data and respect the M2 user authorization boundary.

History must not re-run AI inference simply to reconstruct an existing result.

## M8 — Dashboard & Analytics

**Status:** ACCEPTED

Owns dashboard metrics and analytics derived from verified persisted records/report services.

**Cross-module contract:** dashboard metrics are derived from application data and preserve user scoping. Analytics must not invent values or call the AI provider as a substitute for stored data.

## M9 — Web Application Integration

**Status:** ACCEPTED

Owns the React + TypeScript UI and integration with the verified backend APIs.

**Cross-module contract:** the frontend consumes backend API contracts and handles loading, success, validation, authentication, and error states without duplicating backend business rules.

The frontend must not connect directly to MongoDB.

### Swachhata civic-reporting handoff

M9 also owns the user-facing handoff to the official Swachhata platform for sanitation and waste-management complaints.

Contract:
- EcoSort presents a clear action such as `Report on Swachhata` when appropriate.
- The action opens the official Swachhata platform; EcoSort does not submit the complaint itself.
- EcoSort does not request or store user location for this feature.
- EcoSort does not send EcoSort account data, classification history, or complaint details automatically to Swachhata.
- EcoSort does not store or track the external complaint lifecycle.
- Swachhata remains responsible for citizen complaint submission, municipal routing, and complaint status.
- A direct government API integration is explicitly out of scope unless a later architectural decision approves it.

## M10 — Production Readiness

**Status:** ACCEPTED

Owns end-to-end verification, deployment configuration, production security hardening, observability, operational configuration, and final regression.

**Cross-module contract:** M10 may harden or operationalize existing behavior but must not silently change functional contracts.

## Change Control

If any module needs to change a verified contract:

1. Identify the existing contract.
2. Explain why it is insufficient.
3. Identify affected modules and tests.
4. Propose the new contract.
5. Update this registry and `docs/Architecture.md`.
6. Obtain approval when working interactively.
7. Implement the change.
8. Run focused tests and the full regression suite.
9. Record the decision and checkpoint in `docs/PROGRESS.md`.

## Final Rule

> A later module must preserve earlier verified contracts unless an explicit architectural change says otherwise.
