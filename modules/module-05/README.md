# Module 05 — AI Inference Integration

## Objective
Connect EcoSort's stable classification boundary to a real AI/model inference provider.

## Scope
- Provider/client integration
- Request mapping
- Response mapping
- Timeouts and failure handling
- Provider configuration

## Responsibilities
Own concrete AI integration while preserving the M4 application contract.

## Non-Responsibilities
Changing classification use-case contracts, persistence design, recommendations, reporting, analytics, or frontend behavior.

## Dependencies
M1–M4.

## Inputs
M4 inference requests.

## Outputs
M4-compatible inference results or normalized failures.

## Interfaces / Contracts
Provider-specific details remain internal to M5. M4's accepted inference contract is the integration boundary.

## Data Model
No provider-specific schema may leak into persistent domain records unless explicitly accepted.

## Files Owned
M5 AI integration/client code and tests.

## Files Allowed to Modify
M5-owned files and explicitly required configuration/integration points.

## Files Forbidden to Modify
Earlier module contracts without explicit change control; frontend features; unrelated services.

## Acceptance Criteria
- Real inference integration works with configured credentials/provider.
- Provider failures/timeouts are handled.
- Response validation is enforced.
- No secrets are committed.
- M1–M4 regression remains green.

## Tests
Mocked provider/client tests plus integration tests where appropriate and deterministic.

## Failure / Edge Cases
Timeout, authentication failure, rate limit, malformed response, unavailable provider, invalid prediction.

## Security Considerations
Credentials are externalized. Sensitive provider responses/logs are handled safely.

## Integration Requirements
M4 remains provider-agnostic after M5 implementation.

## Verification Procedure
Focused M5 tests → full regression → configuration/security/diff review → docs → checkpoint.

## Completion Status
NOT STARTED
