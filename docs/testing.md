# Testing Strategy and Evidence

## 1. Test approach

Testing is organised into five complementary levels:

1. pure unit tests for status normalisation and user-service rules;
2. controller/service contract tests for all 12 delivered user stories;
3. H2-backed MyBatis integration tests for public listing visibility;
4. frontend contract tests for routes and critical view behaviour;
5. manual acceptance and deployed end-to-end tests before client sign-off.

The historical project was not developed test-first, so this repository does
not claim full-system TDD. The ownership hardening in Issue
[#37](https://github.com/S13863709935/CP3407/issues/37) does provide auditable
red-green evidence: commit `f9b9593` defines failing seller-ownership tests,
then commit `73e715c` implements the policy and makes the suite pass. Future
changes should continue this red-green-refactor sequence.

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

Local verification on 30 July 2026:

| Suite | Tests | Failures | Errors |
|---|---:|---:|---:|
| `JwtInterceptorTest` | 6 | 0 | 0 |
| `ListingStatusEnumTest` | 3 | 0 | 0 |
| `MarketplaceControllerTest` | 11 | 0 | 0 |
| `WebControllerTest` | 3 | 0 | 0 |
| `GlobalExceptionHandlerTest` | 2 | 0 | 0 |
| `GoodsMapperIntegrationTest` | 3 | 0 | 0 |
| `AddressServiceTest` | 6 | 0 | 0 |
| `CommentServiceTest` | 6 | 0 | 0 |
| `ConversationServiceTest` | 9 | 0 | 0 |
| `GoodsServiceTest` | 8 | 0 | 0 |
| `GoodsServiceAdminTest` | 2 | 0 | 0 |
| `NoticeServiceTest` | 5 | 0 | 0 |
| `OrdersServiceTest` | 7 | 0 | 0 |
| `ResidentScopedServiceTest` | 9 | 0 | 0 |
| `UserServiceTest` | 10 | 0 | 0 |
| **Backend total** | **90** | **0** | **0** |

JaCoCo coverage is **50.1% of project lines**, **50.9% of branches**, and
**49.3% of methods**. The previous 28 July baseline was 31.5% of lines and
26.8% of branches. The increase comes from business-behaviour tests for
authentication, addresses, comments, chat, orders, profiles, favourites,
announcements, feedback, and stable exception responses rather than tests of
entity getters/setters.

The frontend has two Node test suites covering status logic and UI contracts.
All assertions passed, and the production bundle compiled successfully.

## 4. User-story acceptance matrix

| Story | Automated evidence | Important data/edge cases | Manual acceptance still required |
|---|---|---|---|
| US1 | `WebControllerTest`, `UserServiceTest`, `JwtInterceptorTest` | valid, missing and incorrect credentials; header/parameter token, role, signature and unknown account | Login/register in the UI |
| US2 | `us2PublishesAnItemThroughTheGoodsService` | item payload and delegated persistence | Image upload and validation |
| US3 | controller contract and H2 category query | matching category | Confirm production database results |
| US4 | keyword controller, H2 no-result query and frontend empty-result contract | match and no-result response | Search usability |
| US5 | controller contract, `CommentServiceTest`, `ConversationServiceTest` | root/reply tree, recursive deletion, duplicate group prevention, unread count, authenticated history | Browser conversation flow |
| US6 | enum tests, controller update, frontend status suite, H2 visibility query and ownership tests | legacy, canonical, null, rejected value, hidden item, cross-user update | Confirm behaviour against deployed MySQL |
| US7 | seller listing controller contract and red-green ownership tests | user-scoped page, cross-user update/delete, atomic batch validation, administrator moderation | Repeat crafted-request checks against the deployed API |
| US8 | buyer/seller controller contracts and `OrdersServiceTest` | trusted goods/address values, 20-digit order number, resident/admin scopes, empty pages and completed-sales aggregation | Role-authorised state lifecycle and payment sandbox |
| US9 | profile controller/update and `UserServiceTest` | nickname/avatar payload, password success and rejection paths | Avatar upload and persistence |
| US10 | controller contract and `ResidentScopedServiceTest` | add/remove toggle, duplicate prevention, authenticated list, forged owner replacement and batch removal | Browser favourite flow |
| US11 | controller contract and `NoticeServiceTest` | author/date assignment, empty page, query, update and batch removal | Readability and ordering |
| US12 | controller contract and `ResidentScopedServiceTest` | owner assignment, resident/admin read scope, reply update and batch removal | Cross-user mutation hardening and browser history |

## 5. Test data design

Automated tests use deterministic in-memory objects, Mockito doubles and a
disposable H2 database operating in MySQL compatibility mode:

- named users such as `resident`;
- representative categories such as Books, Furniture and Electronics;
- positive, missing, invalid and legacy status values;
- valid, missing, malformed and incorrectly signed JWTs;
- empty search results;
- buyer, seller and administrator order views;
- root comments, replies, unread chat counts and recursive deletion;
- feedback with resident ownership and reply history.

No automated test depends on a developer's MySQL password or mutable local
records. Deployed acceptance testing should use dedicated assessor accounts and
non-sensitive sandbox data.

## 6. Remaining test risks

- H2 provides fast SQL regression coverage but cannot guarantee every
  MySQL-specific behaviour.
- Browser-level interaction and accessibility tests are not yet automated.
- Payment verification requires the Alipay sandbox.
- Coverage now exceeds 50% for both lines and branches, but legacy
  administrator/community modules still prevent whole-system coverage from
  being treated as complete.
- Genuine client acceptance remains open in Issue #27.

Recommended next tests are Testcontainers with MySQL, Playwright/Cypress
journey tests, and access-control cases for the remaining community modules.
