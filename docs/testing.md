# Testing Strategy and Evidence

## 1. Test approach

Testing is organised into four complementary levels:

1. pure unit tests for status normalisation and user-service rules;
2. controller/service contract tests for all 12 delivered user stories;
3. frontend contract tests for routes and critical view behaviour;
4. manual acceptance and deployed end-to-end tests before client sign-off.

The historical project was not developed test-first, so this repository does
not claim full TDD. The current automated suite is a regression safety net, and
future changes should follow red-green-refactor through a failing test on a
feature branch before implementation.

## 2. Repeatable commands

Backend:

```bash
mvn clean verify
```

This compiles the application, executes JUnit/Mockito tests, packages the JAR,
and generates:

- `target/surefire-reports/`
- `target/site/jacoco/`

Frontend:

```bash
npm ci
npm test
npm run build
```

GitHub Actions repeats both sets and uploads the reports/build as artifacts.

## 3. Verified result snapshot

Local verification on 28 July 2026:

| Suite | Tests | Failures | Errors |
|---|---:|---:|---:|
| `ListingStatusEnumTest` | 3 | 0 | 0 |
| `MarketplaceControllerTest` | 11 | 0 | 0 |
| `WebControllerTest` | 3 | 0 | 0 |
| `GoodsServiceTest` | 4 | 0 | 0 |
| `OrdersServiceTest` | 2 | 0 | 0 |
| `ResidentScopedServiceTest` | 4 | 0 | 0 |
| `UserServiceTest` | 3 | 0 | 0 |
| **Backend total** | **30** | **0** | **0** |

JaCoCo baseline coverage is 25.3% of project lines and 23.1% of branches.
This is a truthful baseline rather than a quality target. The tests concentrate
on the assessed resident journeys; legacy administrator and community modules
remain candidates for additional unit tests.

The frontend has two Node test suites covering status logic and UI contracts.
All assertions passed, and the production bundle compiled successfully.

## 4. User-story acceptance matrix

| Story | Automated evidence | Important data/edge cases | Manual acceptance still required |
|---|---|---|---|
| US1 | `WebControllerTest`, `UserServiceTest` | valid, missing and incorrect credentials | Login/register in deployed UI |
| US2 | `us2PublishesAnItemThroughTheGoodsService` | item payload and delegated persistence | Image upload and validation |
| US3 | `us3BrowsesAvailableItemsByCategory` | matching category | Confirm real database results |
| US4 | keyword controller test and frontend empty-result contract | match and no-result response | Search usability |
| US5 | details plus comment contract | item ID and message creation | Buyer/seller conversation |
| US6 | enum tests, controller update, frontend status suite | legacy, canonical, null, rejected value | Off-shelf visibility against MySQL |
| US7 | seller listing controller contract | user-scoped page | Ownership/security attempt |
| US8 | buyer/seller order contracts | purchase/sale lists and initial state | Full state lifecycle and payment sandbox |
| US9 | profile update contract | nickname/avatar fields | Upload and persistence |
| US10 | favourite add/list contract | authenticated list | Duplicate favourite handling |
| US11 | notice list contract | empty/non-empty page | Readability and ordering |
| US12 | feedback add/list contract | reply history | Cross-user access control |

## 5. Test data design

Automated tests use deterministic in-memory objects and Mockito doubles:

- named users such as `resident`;
- representative categories such as Books, Furniture and Electronics;
- positive, missing, invalid and legacy status values;
- empty search results;
- buyer and seller order views;
- feedback with reply history.

No automated test depends on a developer's MySQL password or mutable local
records. Deployed acceptance testing should use dedicated assessor accounts and
non-sensitive sandbox data.

## 6. Remaining test risks

- The MyBatis SQL has not yet been exercised against a disposable database in
  CI.
- Browser-level interaction and accessibility tests are not yet automated.
- Payment verification requires the Alipay sandbox.
- Current line coverage is deliberately reported but not high enough to be
  treated as exemplary whole-system coverage.
- Genuine client acceptance remains open in Issue #27.

Recommended next tests are an H2/Testcontainers mapper suite, Playwright/Cypress
journey tests, and explicit ownership/access-control cases.
