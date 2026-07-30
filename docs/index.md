---
layout: default
title: CP3407 Project Evidence
---

# Local Community Second-hand Marketplace

This site is the evidence hub for the CP3407 project. It links product
planning, design decisions, delivered implementation, automated verification,
tool choices, agile records, and deployment readiness to the repository.

## Assessment evidence map

| Rubric area | Primary evidence | Current status |
|---|---|---|
| Requirements | [Product backlog and acceptance criteria](requirements.md) | 12 delivered stories are prioritised, estimated, budgeted, and traceable |
| Design | [Architecture, database, and interface design](design.md) | Repository models plus online architecture, ERD, and nine-screen Figma prototype are linked; public assessor access must be confirmed |
| Implementation | [Iteration implementation evidence](implementation.md) | Three implementation increments mapped to code; hosted demonstration pending |
| Test | [Testing strategy and results](testing.md) | 90 backend tests pass with 50.1% line and 50.9% branch coverage; two frontend contract suites and CI are configured |
| Version control | [Agile and traceability record](agile.md) | Issues, milestone, feature branches, CI and PR checklist are in use |
| Development tools | [Tool selection and usage](tools.md) | Build, framework, database, UI, test and CI tools are justified |
| Agile engineering | [Iteration reviews and current quality sprint](agile.md) | Scope changes and a genuine hardening sprint are recorded |
| Technical writing | This evidence site and linked repository documents | Rubric evidence is organised as connected, maintainable pages |

## Verified quality snapshot

- Backend: `mvn clean verify` completed with 90 tests, 0 failures and 0
  errors on 30 July 2026.
- JaCoCo reports 50.1% line, 50.9% branch, and 49.3% method coverage.
- Frontend: `npm test` completed two automated contract suites successfully.
- Frontend: `npm run build` produced a deployable production bundle.
- Credentials are read from environment variables in the current source.
- Listing publication values are standardised as `Listed` and `Off-shelf`.
- GitHub Actions repeats backend and frontend verification.

## Honest evidence boundary

The repository does not claim evidence that has not occurred. A public
deployment, demonstration recording, public-link verification for the online
design artefacts, credential rotation, and genuine client acceptance remain tracked in
[Issue #27](https://github.com/S13863709935/CP3407/issues/27).

## Repository navigation

- [Main repository](https://github.com/S13863709935/CP3407)
- [Backend branch](https://github.com/S13863709935/CP3407/tree/Backend)
- [Frontend branch](https://github.com/S13863709935/CP3407/tree/Frontend)
- [GitHub Issues](https://github.com/S13863709935/CP3407/issues)
- [GitHub Actions](https://github.com/S13863709935/CP3407/actions)
- [Security guidance](../SECURITY.md)
- [Contribution workflow](../CONTRIBUTING.md)
