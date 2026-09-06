# Module 02 — Identity & Access

## Objective
Implement user identity, authentication, authorization, and protected-resource boundaries.

## Scope
- User identity model
- Authentication mechanism
- Authorization rules
- Authenticated-user context
- Protected API behavior

## Responsibilities
Own identity and access concerns for the application.

## Non-Responsibilities
Waste classification, AI provider logic, recommendations, reporting, analytics, or frontend feature implementation.

## Dependencies
M1 — Backend Foundation.

## Inputs
Authentication credentials/tokens and authenticated requests.

## Outputs
Authenticated identity and authorization decisions available to downstream services.

## Interfaces / Contracts
Downstream modules receive a stable authenticated-user identity and authorization boundary.

## Data Model
User/identity persistence details are finalized during implementation and recorded in the canonical contract registry.

## Files Owned
M2 identity/security implementation and focused tests.

## Files Allowed to Modify
M2-owned files and M1 integration points required by the contract.

## Files Forbidden to Modify
Future business modules and unrelated frontend features.

## Acceptance Criteria
- Authentication works through the selected mechanism.
- Protected resources reject unauthenticated access.
- User ownership is enforced.
- Security/error cases are tested.
- Existing M1 tests remain passing.

## Tests
Unit and integration tests for authentication, authorization, invalid credentials, and user isolation.

## Failure / Edge Cases
Invalid credentials, expired/invalid tokens, missing identity, unauthorized resource access.

## Security Considerations
Never log credentials/tokens. Never commit secrets. Enforce user-level data isolation.

## Integration Requirements
M3+ must rely on M2 for user identity and authorization rather than reimplementing authentication.

## Verification Procedure
Focused M2 tests → full regression → diff/security review → documentation update → checkpoint.

## Completion Status
NOT STARTED
