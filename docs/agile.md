# Agile Engineering Record

## 1. Working model

The project uses a prioritised product backlog, time-boxed iterations, a GitHub
Project board, Issues, feature branches, automated checks, and evidence-based
review. A story is not treated as accepted only because code or a checked box
exists; it must meet its acceptance criteria and have appropriate verification.

The Definition of Done is recorded in
[requirements.md](requirements.md#6-definition-of-done).

## 2. Product increments

| Iteration | Goal | Planned effort | Outcome |
|---|---|---:|---|
| 1 | End-to-end marketplace prototype | 6 days | Registration/login, publishing, category browsing, details and messaging implemented |
| 2 | Discovery, seller control and transaction lifecycle | 6 days | Search, listing management, order workflow and profile implemented |
| 3 | Retention and communication | 2.5 days | Favourites, announcements and feedback/replies implemented |

The 14.5-day budget, ordering rationale and per-story criteria are maintained in
the backlog rather than duplicated here.

## 3. Backlog refinement decisions

Final acceptance review compared issue titles with delivered behaviour:

- `Specify Item Condition` became `Control Listing Publication Status`.
- `Mark Item as Sold` was retained as superseded history rather than falsely
  claimed as a separate delivered story.
- `Order Checkout & Payment` became the broader delivered order-management
  workflow.
- Event notifications became the implemented announcement feature.
- Listing-specific reports became the implemented feedback/reply workflow.
- Shopping cart and ratings remain visible future scope.

These decisions are recorded in the relevant GitHub Issues and traceability
table.

## 4. Quality & Evidence Sprint

This sprint started on 28 July 2026 after a rubric-based audit found that
working features lacked sufficient security, test, CI and design evidence.

| Issue | Sprint item | Status/acceptance evidence |
|---|---|---|
| [#22](https://github.com/S13863709935/CP3407/issues/22) | Secrets and generated artifacts | Current config uses environment values; generated/IDE files removed from tracking |
| [#23](https://github.com/S13863709935/CP3407/issues/23) | Listing status consistency | Canonical values implemented and tested on both sides |
| [#24](https://github.com/S13863709935/CP3407/issues/24) | Automated tests | 33 backend tests and two frontend suites pass |
| [#25](https://github.com/S13863709935/CP3407/issues/25) | Continuous integration | Quality workflow and evidence artifacts configured |
| [#26](https://github.com/S13863709935/CP3407/issues/26) | Documentation portal | Rubric-linked pages and diagrams created |
| [#27](https://github.com/S13863709935/CP3407/issues/27) | Deployment/client acceptance | Open; requires owner and real reviewer |

## 5. Review and retrospective

### Engineering review

- Frontend test and production build: passed.
- Backend clean verification: passed, 33/33 tests.
- Generated artifacts: no longer tracked.
- Current-source credentials: removed.
- CI: configured; remote run must pass after PR push.
- Deployment/client sign-off: not complete.

### Retrospective

**What worked**

- Rubric-to-evidence mapping exposed the highest-value gaps.
- Isolated unit/contract tests removed dependence on a personal database.
- A single documentation hub reduced scattered evidence.

**What caused difficulty**

- Old generated test classes produced misleading failures until a clean build.
- Frontend/backend values for listing state had diverged.
- Historical work lacked PR, milestone and review records.

**Actions adopted**

- Always use clean CI builds.
- Add a failing test before future fixes where practical.
- Link every new change to an Issue and use a reviewed PR.
- Keep secrets and generated files out of commits.
- Record client review at the time it occurs; never reconstruct it later.

## 6. Evidence boundary

Past client review notes and iteration demonstrations were not present in the
repository and have not been invented. Issue #27 is the explicit gate for
genuine deployment and acceptance evidence.
