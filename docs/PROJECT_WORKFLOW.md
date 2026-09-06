# EcoSort 2.0 — Project Workflow

This document is the canonical engineering workflow for EcoSort 2.0. It exists so that any implementation AI (Antigravity, Cursor, Claude, etc.) can work on the repository without relying on previous chat history or hidden context.

## 1. Core Principles

1. Inspect the repository before changing anything.
2. Treat committed code and canonical documentation as the source of truth.
3. Define architecture and module boundaries before implementation.
4. Implement exactly one module at a time unless explicitly approved otherwise.
5. Preserve accepted module contracts across later modules.
6. Tests must verify contracts and behavior, not merely application startup.
7. Keep implementation scope minimal and avoid speculative features.
8. Never commit secrets, credentials, API keys, tokens, or production connection strings.
9. Build real working functionality; do not replace required behavior with fake success responses or hard-coded production results.
10. Documentation is persistent project state and must be updated after verified changes.

## 2. Canonical Documentation

### `docs/Architecture.md`

Describes system purpose, high-level architecture, technology stack, components, data flow, external integrations, security, deployment, architectural decisions, risks, constraints, and module sequence.

### `docs/module-contracts.md`

Defines accepted interfaces and boundaries between modules, including responsibilities, inputs, outputs, APIs/interfaces, data contracts, dependencies, persistence, errors, security, and compatibility requirements.

### `docs/PROGRESS.md`

Defines the current implementation state and handoff information. Track status, implementation, tests, verification, known issues, decisions, checkpoints, and next steps.

Recommended statuses:

- `NOT STARTED`
- `IN PROGRESS`
- `IMPLEMENTED`
- `VERIFIED`
- `BLOCKED`

A module should become `VERIFIED` only after required focused tests and full regression tests pass.

## 3. Repository Inspection Protocol

Before implementing anything, inspect:

- Project root
- `README.md`
- `docs/Architecture.md`
- `docs/module-contracts.md`
- `docs/PROGRESS.md`
- `pom.xml` or `build.gradle`
- Source tree
- Test tree
- Configuration files
- Existing module READMEs
- Existing tests
- Git status
- Relevant recent changes

For Spring Boot, also inspect the application class, package structure, controllers, services, repositories, domain/model classes, configuration, exception handling, security configuration, and test configuration.

Do not begin implementation until the relevant structure is understood.

## 4. Module Structure

Each module gets its own documentation and focused verification tests. Recommended structure:

```text
modules/
├── module-01/
│   ├── README.md
│   └── tests/
│       └── Module01Test.java
├── module-02/
│   ├── README.md
│   └── tests/
│       └── Module02Test.java
└── ...
```

Java source remains in the normal Maven/Gradle source tree unless architecture explicitly requires otherwise.

## 5. Module README Standard

Every module README should contain:

```text
# Module XX — <Name>

## Objective
## Scope
## Responsibilities
## Non-Responsibilities
## Dependencies
## Inputs
## Outputs
## Interfaces / Contracts
## Data Model
## Files Owned
## Files Allowed to Modify
## Files Forbidden to Modify
## Acceptance Criteria
## Tests
## Failure / Edge Cases
## Security Considerations
## Integration Requirements
## Verification Procedure
## Completion Status
```

## 6. Module Lifecycle

### Phase A — Understand

Read the canonical docs, current module README, relevant source, and relevant tests. Summarize the current state.

### Phase B — Plan

Identify scope, reusable code, interfaces, dependencies, files to change, tests required, risks, and compatibility with previous modules. If a requested change conflicts with an existing contract, stop and report the conflict.

### Phase C — Implement

Implement only the current module. Follow the architecture and contract. Keep changes scoped. Do not implement future modules or unrelated refactors.

### Phase D — Focused Verification

Run module-specific tests covering happy paths, invalid inputs, errors, boundaries, contracts, security, persistence, and integration behavior as applicable.

### Phase E — Regression Verification

Run the complete existing test suite. New work must not break previously verified behavior. Never delete or weaken existing tests just to obtain a passing result.

### Phase F — Review

Inspect `git status` and `git diff`. Check for accidental files, secrets, debugging code, dead code, unnecessary dependencies, unrelated changes, contract violations, and future-module implementation.

### Phase G — Documentation

Update the module README, `docs/PROGRESS.md`, and—when applicable—`docs/module-contracts.md` and `docs/Architecture.md`. Documentation must match verified reality.

### Phase H — Checkpoint

After verification, create a focused Git checkpoint when appropriate. Recommended format:

```text
feat(module-XX): implement <short description>
```

## 7. Change-Control Rules

An AI must not silently change a previous module's API, database contract, selected technology, security controls, or tests.

If a breaking change is necessary:

1. Identify the existing contract.
2. Explain why it is insufficient.
3. Propose the change.
4. Identify affected modules, tests, and documentation.
5. Obtain approval when working interactively.
6. Update canonical documentation.
7. Implement the change.
8. Run full regression tests.

## 8. Java / Spring Boot Conventions

Unless architecture says otherwise:

- Use Maven or Gradle consistently.
- Follow standard Spring Boot package organization.
- Keep controllers thin.
- Put business logic in services.
- Use repositories for persistence access.
- Prefer constructor injection.
- Use DTOs at API boundaries where appropriate.
- Validate external input.
- Centralize exception handling where appropriate.
- Externalize configuration.
- Never hard-code secrets.
- Keep tests deterministic.

Typical layering:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MongoDB / External System
```

External AI inference should have a dedicated abstraction/client boundary rather than being scattered across controllers.

## 9. MongoDB Rules

- Isolate database access behind repository abstractions where useful.
- Do not scatter MongoDB-specific queries through business logic.
- Define clear domain/document boundaries.
- Validate required fields.
- Handle missing documents explicitly.
- Consider indexes for frequently queried fields.
- Never expose database credentials in source code.
- Tests must not accidentally write to production databases.

## 10. AI / ML Integration Rules

AI functionality is an explicit system component.

Define and preserve:

- Input format
- Output format
- Confidence representation
- Error behavior
- Timeout behavior
- Invalid prediction behavior
- Fallback behavior
- Model/service boundary

The model implementation may change without forcing the rest of the application to change, provided the accepted interface remains stable.

Do not hard-code fake classification results in production code.

## 11. External Service Rules

For every external API/service:

- Define a client/interface boundary.
- Keep credentials outside source control.
- Configure timeouts.
- Handle failures explicitly.
- Validate responses.
- Keep external calls in the appropriate layer.
- Test failure behavior.
- Use mocks/stubs for deterministic unit tests.
- Use integration tests for actual connectivity when required.

## 12. Git Workflow

Before starting:

```bash
git status
```

During implementation:

```bash
git diff
```

Before completion:

```bash
git status
git diff
```

After verification:

```bash
git add <relevant files>
git commit -m "feat(module-XX): <description>"
```

Never commit `.env`, credentials, API keys, generated junk, or personal IDE artifacts.

## 13. Progress / Handoff Format

`docs/PROGRESS.md` should let another AI continue without reading old chat messages.

Recommended format:

```text
## Module XX — <Name>

Status: VERIFIED

Implemented:
- ...

Tests:
- ...

Verification:
- Focused tests: PASS
- Full regression: PASS

Known Issues:
- None

Important Decisions:
- ...

Checkpoint:
- <commit SHA>

Next:
- Module XX+1
```

## 14. AI Completion Report

After completing a module, report:

```text
Module: MXX — <Name>
Status: VERIFIED

Implemented:
- ...

Files changed:
- ...

Tests added/updated:
- ...

Focused tests:
- PASS

Full regression:
- PASS

Architecture/contract changes:
- None
  OR
- <explicitly describe them>

Documentation updated:
- ...

Git checkpoint:
- <commit SHA>

Next module:
- MXX+1 — <Name>
```

Never report `VERIFIED` if required checks failed.

## 15. Definition of Done

A module is `VERIFIED` only when all applicable conditions are satisfied:

- [ ] Architecture respected
- [ ] Module contract respected
- [ ] Implementation complete
- [ ] No future-module functionality added
- [ ] Focused tests exist
- [ ] Focused tests pass
- [ ] Full regression suite passes
- [ ] Error cases tested
- [ ] Security requirements satisfied
- [ ] No secrets committed
- [ ] Diff reviewed
- [ ] Module README updated
- [ ] `docs/PROGRESS.md` updated
- [ ] `docs/module-contracts.md` updated if necessary
- [ ] `docs/Architecture.md` updated if necessary
- [ ] Git checkpoint created when appropriate

## 16. Regression Preservation Rule

Once a module is verified, its behavior becomes part of the compatibility surface. Later modules must preserve existing APIs, domain behavior, persistence behavior, authentication/authorization behavior, tests, and module contracts.

A later module requiring a breaking change must explicitly identify it as an architectural change.

## 17. Testing Philosophy

Prioritize:

1. Unit tests
2. Service-layer tests
3. Repository/data-access tests
4. Controller/API tests
5. Integration tests
6. End-to-end tests where appropriate

Use mocks only where they provide appropriate isolation. Do not mock the system under test itself.

Tests should not depend on developer-specific paths, personal credentials, undocumented local configuration, or uncontrolled external services unless explicitly designed as integration tests.

## 18. Emergency / Blocked State

If implementation is blocked by missing credentials, unavailable services, ambiguous requirements, contract conflicts, architecture conflicts, broken existing code, or missing dependencies:

1. Stop at the current module boundary.
2. Document the blocker.
3. Update `docs/PROGRESS.md`.
4. Explain what information/action is required.
5. Preserve the repository in a clean, understandable state.

## 19. Final Rule

> **Do not trust chat memory. Trust the repository.**

A fresh AI session should be able to read:

```text
docs/Architecture.md
docs/module-contracts.md
docs/PROGRESS.md
modules/module-XX/README.md
```

and understand what EcoSort 2.0 is, how it is architected, what is implemented, what contracts must be preserved, what module is active, what remains, and how the current work is verified.
