# Module 06 — Disposal Recommendations

## Objective
Turn verified waste classifications into useful disposal, recycling, or handling recommendations.

## Scope
- Recommendation domain model
- Classification-to-recommendation rules
- Recommendation service/use case
- Invalid/unknown category handling

## Responsibilities
Own deterministic recommendation logic and its application contract.

## Non-Responsibilities
AI inference, authentication implementation, persistence internals, reporting, analytics, or frontend UI.

## Dependencies
M1–M5 and verified classification contracts.

## Inputs
Verified classification result and applicable user/context information.

## Outputs
Stable disposal/recycling recommendation data.

## Interfaces / Contracts
Recommendation request/result contract consumed by M9 and potentially M7/M8.

## Data Model
Recommendation data is defined at the application boundary; persistent storage is added only if required by the accepted architecture.

## Files Owned
M6 recommendation logic and tests.

## Files Allowed to Modify
M6-owned files and required integration points.

## Files Forbidden to Modify
AI provider internals, authentication internals, reporting/analytics implementation, unrelated UI.

## Acceptance Criteria
- Recommendations are deterministic for supported categories.
- Unknown/unsupported classifications are handled explicitly.
- Business rules are testable.
- Earlier regression remains green.

## Tests
Rule/service tests covering supported, unsupported, malformed, and boundary classification results.

## Failure / Edge Cases
Unknown category, missing classification, invalid confidence/result, conflicting rule inputs.

## Security Considerations
Respect authenticated-user scope where user data is involved; do not trust client-supplied classification as authoritative when server-side data exists.

## Integration Requirements
M9 consumes the recommendation contract; no frontend reimplementation of recommendation rules.

## Verification Procedure
Focused M6 tests → full regression → contract/diff review → docs → checkpoint.

## Completion Status
NOT STARTED
