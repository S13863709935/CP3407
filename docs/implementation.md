# Implementation Evidence

## 1. Delivered product

The application is a two-role local marketplace. Residents can register,
publish and discover items, communicate, save favourites, manage listings,
place and track orders, maintain a profile, read announcements, and submit
feedback. Administrators can moderate and manage platform records.

The backend is stored on the
[Backend branch](https://github.com/S13863709935/CP3407/tree/Backend) and the
Vue application is stored on the
[Frontend branch](https://github.com/S13863709935/CP3407/tree/Frontend).

## 2. Iteration evidence

| Iteration | Goal | Delivered stories | Primary implementation evidence |
|---|---|---|---|
| 1 | End-to-end marketplace prototype | US1, US2, US3, US5 | `WebController`, `GoodsController`, `GoodsService`, category filtering, item details and comments |
| 2 | Discovery, seller control and transaction lifecycle | US4, US6, US7, US8, US9 | Search view/query, listing status normalisation, seller listing view, orders workflow and profile view |
| 3 | Retention and platform communication | US10, US11, US12 | Collect/favourites, notices, feedback history and reply field |

Detailed priorities, estimates, budgets and acceptance criteria are in the
[requirements backlog](requirements.md).

## 3. Feature-to-code map

| Story | Backend evidence | Frontend evidence |
|---|---|---|
| US1 Registration/login | `WebController`, `UserService`, JWT interceptor | `Login.vue`, `Register.vue`, token request wrapper |
| US2 Publish item | `GoodsController.add`, `GoodsService.add`, upload controllers | `front/AddGoods.vue` |
| US3 Browse category | `GoodsMapper.selectFrontAll` category filter | `front/Home.vue` |
| US4 Keyword search | `GoodsMapper.selectFrontAll` name filter | `front/Search.vue` |
| US5 Details/messaging | `GoodsController`, `CommentController`, chat services | `front/GoodsDetail.vue`, `front/Chat.vue` |
| US6 Publication status | `ListingStatusEnum`, `GoodsService`, listed query | `listingStatus.js`, `front/AddGoods.vue` |
| US7 My listings | user-scoped `GoodsService.selectPage` | `front/Goods.vue` |
| US8 Orders | `OrdersController`, `OrdersService`, order mapper | `front/Orders.vue`, purchase flow in details |
| US9 Profile | `UserController.updateById` | `front/Person.vue` |
| US10 Favourites | `CollectController`, `CollectService` | `front/Collect.vue` |
| US11 Announcements | `NoticeController`, `NoticeService` | `front/Notice.vue` |
| US12 Feedback/replies | `FeedbackController`, user-scoped service | `front/Feedback.vue`, `front/UserFeedback.vue` |

Frontend files can be inspected directly on the
[Frontend source tree](https://github.com/S13863709935/CP3407/tree/Frontend/src).

## 4. Quality hardening increment

The Quality & Evidence Sprint is a genuine additional increment, not a
retroactive claim. It:

- removed current-source credentials and introduced environment configuration;
- removed generated Maven and IDE output from version control;
- fixed inconsistent listing publication values;
- fixed safe handling of empty search responses;
- replaced database-dependent placeholder tests with deterministic coverage;
- added frontend contract tests, JaCoCo reporting and GitHub Actions;
- expanded behaviour and security regression coverage from 39 to 90 backend
  tests and from 31.5% to 50.1% of project lines;
- preserved stable empty-state lists for orders, favourites and feedback;
- organised the assessment evidence into connected technical pages.

The sprint is tracked by
[Issues #22-#27](https://github.com/S13863709935/CP3407/milestone/1).

## 5. Build evidence

Verified on 30 July 2026:

```text
Backend: mvn clean verify
Result: 90 tests, 0 failures, 0 errors; executable JAR produced
JaCoCo: 50.1% lines, 50.9% branches, 49.3% methods

Frontend: npm test
Result: two contract suites passed

Frontend: npm run build
Result: production bundle produced successfully
```

The frontend build reports bundle-size warnings. They do not prevent
deployment, but route-level dependency optimisation is a future performance
improvement.

## 6. Demonstration and client evidence

A hosted demonstration and genuine client review have not yet been recorded.
They are intentionally not marked complete. The final evidence pack should add:

1. deployment URL and release identifier;
2. screenshots or a video showing all 12 stories;
3. reviewer name/role, date, feedback and decisions;
4. accepted criteria and unresolved defects.

This work remains in
[Issue #27](https://github.com/S13863709935/CP3407/issues/27).
