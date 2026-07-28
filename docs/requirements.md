# Requirements and Product Backlog

## 1. Product goal

The Local Community Second-hand App helps residents exchange used items within
their community. The product must make listings easy to discover, support
direct buyer-seller communication, and maintain a trustworthy environment
through authenticated accounts, listing controls, order management, system
announcements, and a feedback channel.

The planned scope contains 12 user stories with a total estimated effort of
**14.5 developer-days**.

## 2. Prioritisation method

Priority is an ordered business rank: **a lower number means a higher delivery
priority**. Equal values indicate stories with the same business priority.

The ranking follows these principles:

1. Establish identity and supply first: users must be able to register and
   publish items before the marketplace has value.
2. Deliver the buyer's core discovery and communication journey next.
3. Add seller inventory controls, order processing, and profile trust signals
   after the core trading flow works.
4. Add retention, platform communication, and user feedback capabilities in
   the final iteration.

Effort estimates include implementation and developer testing. They do not
include deployment lead time or client scheduling.

## 3. Prioritised backlog

| ID | User story | Priority | Effort | Iteration | Priority rationale |
|---|---|---:|---:|---:|---|
| US1 | As a resident, I want to register and log in using my account, so that the system can maintain a secure and accountable trading environment. | 10 | 1 day | 1 | Authentication is required before users can publish, manage, save, comment on, or submit feedback. |
| US2 | As a seller, I want to upload item photos, set a price, and write a description, so that my neighbours can see the idle items I want to sell. | 10 | 2 days | 1 | Listings create marketplace supply and form the main business object used by later stories. |
| US3 | As a buyer, I want to view all available items sorted by categories in a list, so that I can quickly find what I need locally. | 20 | 1 day | 1 | Category browsing provides the first usable product-discovery path. |
| US4 | As a buyer, I want to search for specific items using keywords via a search bar, so that I can find exactly what I am looking for without scrolling. | 25 | 1.5 days | 2 | Search improves discovery after the basic listing and category flows are stable. |
| US5 | As a buyer, I want to view the detailed information of an item and leave a message below it, so that I can communicate trading details with the seller. | 30 | 2 days | 1 | Item details and messaging complete the minimum buyer-seller interaction loop. |
| US6 | As a seller, I want to place my listing on-shelf or off-shelf, so that I can control whether buyers can discover an item without deleting its information. | 35 | 0.5 days | 2 | Publication control lets sellers temporarily remove unavailable listings while retaining their data. |
| US7 | As a seller, I want to view all the items I have published, so that I can manage my listing inventory. | 40 | 1 day | 2 | Seller inventory management becomes necessary once multiple listings can be published. |
| US8 | As a buyer, I want to place an order using a saved delivery address and track its status, so that I can complete and manage a purchase. | 40 | 2 days | 2 | Order processing turns product discovery into a complete transaction and supports both buyer and seller actions. |
| US9 | As a user, I want to upload an avatar and update my nickname, so that other neighbours can recognise me and trust my listings. | 45 | 1 day | 2 | Profile customisation improves trust but is not required for the first trading flow. |
| US10 | As a buyer, I want to add interesting items to my favourites list, so that I can easily find them later when making a purchasing decision. | 50 | 1 day | 3 | Favourites improve return visits after discovery and listing management are complete. |
| US11 | As a resident, I want to read system announcements in the app, so that I stay informed about platform updates, community information, and trading guidance. | 60 | 0.5 days | 3 | Announcements provide a lightweight communication channel after the core marketplace is operational. |
| US12 | As a resident, I want to submit feedback and view the administrator's reply, so that questions and concerns can be handled transparently. | 70 | 1 day | 3 | Feedback creates a support loop and gives administrators evidence for future product improvements. |

## 4. Iteration plan and budget

| Iteration | Goal | Included stories | Planned effort | Deliverable |
|---|---|---|---:|---|
| Iteration 1 | Deliver an end-to-end marketplace prototype | US1, US2, US3, US5 | 6 days | A resident can create an account, publish an item, browse by category, open its details, and communicate with the seller. |
| Iteration 2 | Complete discovery, seller controls, and the transaction lifecycle | US4, US6, US7, US8, US9 | 6 days | Buyers can search and place orders, while sellers can control listings and users can maintain trusted profiles. |
| Iteration 3 | Add retention and platform communication | US10, US11, US12 | 2.5 days | Buyers can save items, residents can read announcements, and users can submit feedback and review replies. |
| **Total** |  | **12 stories** | **14.5 days** | Complete planned product scope. |

The first iteration has the largest budget because it establishes the shared
database entities, authentication, file upload, and the first complete
frontend-backend workflow. Later iterations reuse that foundation and
therefore require less integration effort.

## 5. Acceptance criteria

### US1 - Community Account Registration and Login

- Given a unique username and valid required fields, registration creates a
  resident account and directs the user to login.
- Given valid credentials, login returns a successful response and establishes
  an authenticated session/token.
- Invalid, missing, or incorrect credentials do not authenticate the user and
  produce a clear error message.
- Authenticated-only operations reject requests without a valid token.

### US2 - Publish Second-hand Items

- An authenticated seller can enter a name, positive price, category,
  description, address, and image.
- A successful submission creates one listing owned by the authenticated
  seller.
- Required or invalid fields are rejected with a clear validation message.
- The uploaded image and listing details are visible after publication and
  approval.

### US3 - Browse Items by Category

- Buyers can choose a category and see only available, approved listings in
  that category.
- Changing or clearing the category refreshes the result list correctly.
- A category with no matching items displays a stable, informative empty state.
- Off-shelf or rejected items are excluded from available browsing results.

### US4 - Search Items by Keyword

- Entering a keyword returns available listings whose names contain that
  keyword.
- Search can be combined with pagination without losing the keyword.
- Empty search input returns the normal available-listing view.
- A keyword with no matches returns an empty state without a server or frontend
  error.

### US5 - Item Details and Messaging

- Selecting a listing opens its image, price, seller, description, location,
  status, and other relevant details.
- An authenticated buyer can post a non-empty message on the selected listing.
- The new message is associated with the correct listing and user and appears
  in the listing's comment thread.
- A user can start a direct chat with the listing's seller.

### US6 - Control Listing Publication Status

- The listing form lets the seller choose On-shelf or Off-shelf.
- The selected publication status is persisted with the listing and returned by
  the API.
- Only on-shelf, approved listings appear in the public marketplace.
- Editing the publication status does not delete or alter unrelated listing
  information.

### US7 - Manage My Listings

- An authenticated seller sees only listings owned by that seller.
- The view includes the listing's approval state, availability, date, and
  management actions.
- The seller can edit or delete an owned listing.
- One user cannot update or delete another user's listing.

### US8 - Place and Manage Orders

- An authenticated buyer can select a delivery address and create an order from
  an available listing.
- The order records the item, buyer, seller, price, address, order number, time,
  and current status.
- Buyers can view purchases, sellers can view sales, and both views can be
  filtered by order status.
- Permitted users can progress the order through Pending Payment, To Ship, To
  Receive, Completed, or Cancelled according to their role in the transaction.

### US9 - User Profile Customisation

- An authenticated user can upload and preview a profile avatar.
- The user can update the displayed nickname while the login username remains
  unchanged.
- Saved changes persist after logout and login.
- Other users see the updated identity where seller information is displayed.

### US10 - Save/Favourite Items

- An authenticated buyer can add a listing to favourites from its detail page.
- Repeating the action does not create duplicate favourite records.
- The favourites page shows only the current user's saved listings.
- The user can remove a favourite and the list/count updates correctly.

### US11 - View System Announcements

- Residents can open the announcements page from the application navigation.
- The page displays each announcement's title, content, publication time, and
  author where available.
- Newly published administrator announcements appear without a frontend code
  change.
- An empty announcement list is displayed without an application error.

### US12 - Submit Feedback and View Replies

- An authenticated resident can submit a feedback subject and content, with
  optional phone and email contact details.
- The system records the feedback owner and submission time.
- Administrators can view submitted feedback and save a reply.
- Residents can view only their own feedback history and the associated
  administrator replies.

## 6. Definition of Done

A story is Done only when:

1. All acceptance criteria are implemented and demonstrable.
2. Backend and frontend production builds succeed.
3. Automated tests cover the happy path, a negative path, and an important
   edge case.
4. Acceptance-test evidence is recorded in the testing documentation.
5. No credentials, private keys, or environment-specific secrets are committed.
6. Relevant documentation, screenshots, and traceability links are updated.
7. The completed work is reviewed in the iteration review and reflected on in
   the retrospective.

## 7. Requirements traceability and current gap analysis

This table separates the planned requirement from what is currently evidenced
in the repository. A checked README entry alone is not treated as acceptance
evidence.

| Story | GitHub planning evidence | Current implementation evidence | Automated test evidence | Current assessment |
|---|---|---|---|---|
| US1 | Issues [#7](https://github.com/S13863709935/CP3407/issues/7), [#8](https://github.com/S13863709935/CP3407/issues/8), and [#9](https://github.com/S13863709935/CP3407/issues/9) | `/register`, `/login`, token interceptor, Vue login/register views | Login tests | Implemented; expand registration and authorisation tests |
| US2 | Issues [#10](https://github.com/S13863709935/CP3407/issues/10), [#11](https://github.com/S13863709935/CP3407/issues/11), and [#12](https://github.com/S13863709935/CP3407/issues/12) | Goods create/update APIs, file upload, AddGoods view | Limited controller tests | Implemented; strengthen validation and ownership tests |
| US3 | Issue [#13](https://github.com/S13863709935/CP3407/issues/13) | Category filters and available-listing query | Category request tests | Implemented; add result-content assertions |
| US4 | Issue [#6](https://github.com/S13863709935/CP3407/issues/6) and bug [#18](https://github.com/S13863709935/CP3407/issues/18) | Keyword query and Search view | Keyword request tests | Implemented; add assertions for matching and empty result sets |
| US5 | Issue [#14](https://github.com/S13863709935/CP3407/issues/14) | GoodsDetail, comment tree, and chat group features | None mapped to US5 | Implemented; acceptance and integration tests required |
| US6 | Issue [#1](https://github.com/S13863709935/CP3407/issues/1); issue [#3](https://github.com/S13863709935/CP3407/issues/3) is retained as superseded history | Canonical listing status module/enum, Goods service normalisation, AddGoods controls, and listed-only mapper query | Backend enum tests, controller contract, and frontend status suite | Implemented; add a disposable-database visibility integration test |
| US7 | Issue [#2](https://github.com/S13863709935/CP3407/issues/2) | Seller Goods view and user-filtered listing service | None mapped to US7 | Implemented; ownership tests required |
| US8 | Issue [#16](https://github.com/S13863709935/CP3407/issues/16) | GoodsDetail order creation, buyer/seller order views, order status workflow, and payment entry point | None mapped to US8 | Implemented; authorisation and lifecycle tests required |
| US9 | Issue [#4](https://github.com/S13863709935/CP3407/issues/4) | Person view supports avatar and profile updates | None | Implemented; persistence and validation tests required |
| US10 | Issues [#5](https://github.com/S13863709935/CP3407/issues/5) and [#19](https://github.com/S13863709935/CP3407/issues/19) | Collect API, favourites page, and favourite toggle | Unauthenticated negative test only | Implemented; authenticated behaviour tests required |
| US11 | Issue [#20](https://github.com/S13863709935/CP3407/issues/20) | Notice API, administrator notice management, and resident Notice view | None mapped to US11 | Implemented; add acceptance tests |
| US12 | Issue [#21](https://github.com/S13863709935/CP3407/issues/21) | Feedback submission, user-specific feedback history, administrator management, and reply field | None mapped to US12 | Implemented; add access-control tests |

## 8. Review notes

- The GitHub Issues establish useful planning history. Issues #1, #16, #20,
  and #21 are aligned with the revised backlog and now record scope, estimates,
  iterations, and acceptance criteria. Earlier technical issues still need
  richer acceptance evidence and discussion.
- Completed dates, client review notes, and acceptance results should be added
  only when supported by genuine evidence.
- Issue #3 is retained as superseded history rather than being claimed as an
  independently delivered feature. Issues #15 and #17 remain open and are
  explicitly identified as future scope.
- The implementation is aligned with all 12 revised stories. Deterministic
  controller/service tests now cover the story contracts, but deployed
  acceptance, deeper validation/authorisation cases, and genuine client review
  remain required before every story meets the Definition of Done.
