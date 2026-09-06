# Module 04 — Classification Application

## Objective
Implement the application-level waste classification use case and establish the stable AI inference boundary.

## Scope
- Classification request/response models
- Input validation
- Classification service/use case
- Inference interface
- Result normalization and error contract

## Responsibilities
Orchestrate classification without depending on a concrete AI provider.

## Non-Responsibilities
Concrete model/provider implementation, recommendations, reporting, analytics, or frontend UI.

## Dependencies
M1, M2, and M3.

## Inputs
Validated waste image/input reference and authenticated user context.

## Outputs
Stable classification result or explicit classification failure.

## Interfaces / Contracts
The AI inference interface is owned by the application and consumed by M5. Exact DTOs/error types are recorded when finalized.

## Data Model
Classification persistence uses M3 contracts; M4 must not bypass M3 repositories.

## Files Owned
M4 classification application code and focused tests.

## Files Allowed to Modify
M4-owned files and explicitly required integration points.

## Files Forbidden to Modify
Concrete AI provider code before M5 and unrelated features.

## Acceptance Criteria
- Classification use case is independently testable.
- Provider-specific details are absent from application logic.
- Invalid input and inference failures are represented explicitly.
- Existing contracts remain intact.

## Tests
Service/unit tests for valid input, invalid input, inference success/failure, confidence/result validation, and persistence integration as applicable.

## Failure / Edge Cases
Invalid input, empty result, malformed inference result, low/invalid confidence, provider timeout/error propagated through the boundary.

## Security Considerations
Validate inputs and never trust provider output blindly.

## Integration Requirements
M5 implements the accepted inference interface without forcing M4 to know provider-specific details.

## Verification Procedure
Focused M4 tests → full regression → contract/diff review → update docs → checkpoint.

## Completion Status
NOT STARTED
