# EcoSort 2.0 — Module Contracts

> Status: DESIGN PHASE

This file is the canonical registry of cross-module contracts.

## Contract Rules

1. A verified module contract is stable by default.
2. Later modules must consume earlier contracts rather than silently redefining them.
3. Breaking changes require an explicit architectural decision and regression verification.
4. Implementation details that are private to a module do not belong here unless another module depends on them.
5. APIs, DTOs, domain contracts, repository abstractions, AI inference contracts, and external-service boundaries belong here when they cross module boundaries.

## Modules

No module contracts have been accepted yet.

The module sequence and contracts will be added after the EcoSort 2.0 functional scope and architecture are finalized.
