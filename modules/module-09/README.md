# Module 09 — Web Application Integration

## Objective
Build the React + TypeScript web application and integrate the verified backend functionality into a complete user-facing flow.

## Scope
- Application shell/navigation
- Authentication UI
- Waste identification UI
- Classification/recommendation result UI
- History/reports UI
- Dashboard UI
- Loading, validation, and error states
- Backend API client integration

## Responsibilities
Own frontend presentation, client-side orchestration, and API integration.

## Non-Responsibilities
MongoDB access, backend business rules, AI provider calls, or authentication bypasses.

## Dependencies
M1–M8 and all verified backend API contracts required by the UI.

## Inputs
User interaction and backend API responses.

## Outputs
Responsive web application behavior and user-facing states.

## Interfaces / Contracts
Consumes the accepted backend REST contracts without silently redefining them.

## Data Model
Frontend types mirror API DTOs as needed; frontend state is not treated as authoritative persistence.

## Files Owned
Frontend application and M9-focused tests.

## Files Allowed to Modify
Frontend files and explicitly required API integration configuration.

## Files Forbidden to Modify
Backend business logic merely to accommodate UI convenience without contract review.

## Acceptance Criteria
- Main user flow works end-to-end against the backend.
- Authentication and protected pages behave correctly.
- Classification and recommendations render real backend results.
- History/reports/dashboard consume real data.
- Error/loading/empty states are handled.
- Earlier backend regression remains green.

## Tests
Component/API integration tests and appropriate end-to-end coverage for critical flows.

## Failure / Edge Cases
Network failure, expired session, invalid upload/input, empty history, backend errors, slow AI response.

## Security Considerations
No secrets in frontend source. Do not trust client-side authorization. Do not expose database/provider credentials.

## Integration Requirements
The frontend must use the backend as the source of truth for protected business operations.

## Verification Procedure
Focused frontend tests → backend regression → end-to-end verification → diff/security review → docs → checkpoint.

## Completion Status
NOT STARTED
