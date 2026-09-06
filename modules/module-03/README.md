# Module 03 — Waste Domain & Persistence

## Objective
Define the core waste domain and persist classification/activity records in MongoDB.

## Scope
- Waste and classification domain models
- MongoDB document mapping
- Repository abstractions and implementations
- Validation and persistence behavior
- Required indexes

## Responsibilities
Own the application's durable waste/classification data model and repository boundary.

## Non-Responsibilities
Authentication implementation, AI provider calls, recommendation rules, dashboard UI, or frontend implementation.

## Dependencies
M1 and M2.

## Inputs
Validated application data associated with an authenticated user.

## Outputs
Persisted records and repository operations consumed by later modules.

## Interfaces / Contracts
Repository/service contracts and persisted domain representation used by M4, M6, M7, and M8.

## Data Model
Exact fields, identifiers, indexes, validation rules, and ownership constraints are finalized during M3 and recorded in `docs/module-contracts.md`.

## Files Owned
M3 domain, persistence, repository code, and tests.

## Files Allowed to Modify
M3-owned files plus explicitly required M1/M2 integration points.

## Files Forbidden to Modify
AI provider implementation, recommendations, reports, analytics, and frontend features.

## Acceptance Criteria
- Domain model is explicit and validated.
- MongoDB persistence works.
- Repository boundary is testable.
- User ownership is enforced.
- Persistence failure cases are handled.
- M1/M2 regression remains green.

## Tests
Repository/data-access tests, domain validation tests, ownership tests, and relevant integration tests.

## Failure / Edge Cases
Missing records, duplicate identifiers, invalid data, persistence failures, cross-user access attempts.

## Security Considerations
Never expose database credentials. Enforce user scoping and validate persisted input.

## Integration Requirements
Later modules use repository/domain contracts rather than scattering MongoDB queries.

## Verification Procedure
Focused M3 tests → full regression → inspect persistence changes/diff → update contracts/progress → checkpoint.

## Completion Status
NOT STARTED
