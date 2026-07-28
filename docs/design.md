# System Design

## 1. Design goals

The system supports accountable local second-hand trading while keeping the
first release achievable within a 14.5 developer-day budget. The design
separates the browser UI, HTTP/WebSocket API, business services, persistence,
and external integrations so each area can be changed and tested independently.

The main quality goals are:

1. authenticated and role-aware trading;
2. clear ownership of listings, orders, favourites, and feedback;
3. a simple deployment model appropriate for a student project;
4. traceability from every delivered user story to a UI route and API;
5. graceful handling of empty results and unavailable external services.

## 2. Architectural design

```mermaid
flowchart LR
    Resident["Resident browser"]
    Admin["Administrator browser"]
    Vue["Vue 2 single-page application<br/>Vue Router + Element UI"]
    HTTP["Axios HTTP client<br/>JWT token header"]
    WS["WebSocket chat"]
    API["Spring Boot controllers<br/>REST API :9090"]
    Auth["JWT interceptor<br/>role and identity checks"]
    Services["Domain services<br/>users, goods, orders,<br/>comments, feedback"]
    MyBatis["MyBatis mappers<br/>PageHelper"]
    MySQL[("MySQL database")]
    Files[("Uploaded files")]
    Alipay["Alipay sandbox"]

    Resident --> Vue
    Admin --> Vue
    Vue --> HTTP
    Vue --> WS
    HTTP --> API
    WS --> API
    API --> Auth
    Auth --> Services
    API --> Services
    Services --> MyBatis
    MyBatis --> MySQL
    Services --> Files
    Services --> Alipay
```

### Component responsibilities

| Component | Responsibility | Design justification |
|---|---|---|
| Vue views and router | User journeys, form validation, state presentation | Keeps resident/admin interfaces responsive and route-oriented |
| Axios request wrapper | Base URL, JWT header, response/error handling | Prevents repeated networking code in views |
| Spring controllers | Stable HTTP contract and response envelope | Keeps transport concerns separate from business rules |
| JWT interceptor | Authentication before protected controllers execute | Central enforcement is less error-prone than checks in each endpoint |
| Services | Ownership, defaults, workflow and aggregation rules | Business behaviour can be unit tested without a web server |
| MyBatis mappers | Explicit SQL and object mapping | Suitable for the existing relational schema and query-focused application |
| MySQL | Persistent transactional data | Orders, users and listings require relational consistency |
| File store | Item images and avatars | Separates binary data from relational records |
| Alipay adapter | Optional sandbox payment entry | Isolates an external service from the core marketplace flow |

## 3. Request and trust flow

```mermaid
sequenceDiagram
    actor User
    participant UI as Vue UI
    participant API as Spring Controller
    participant JWT as JWT Interceptor
    participant Service as Domain Service
    participant DB as MySQL/MyBatis

    User->>UI: Submit protected action
    UI->>API: HTTP request + token
    API->>JWT: Intercept request
    JWT->>DB: Load account used to verify signature
    DB-->>JWT: Account and role
    JWT-->>API: Authenticated request
    API->>Service: Validated command/query
    Service->>DB: Read or write owned data
    DB-->>Service: Result
    Service-->>API: Domain result
    API-->>UI: { code, msg, data }
    UI-->>User: Success, empty state, or actionable error
```

Credentials and payment keys are environment variables. The current source
contains no live values, but every value exposed in earlier history must still
be rotated by the repository owner.

## 4. Database design

The following logical ER model focuses on the tables used by the 12-story
release.

```mermaid
erDiagram
    USER ||--o{ GOODS : publishes
    USER ||--o{ ADDRESS : owns
    USER ||--o{ COLLECT : saves
    GOODS ||--o{ COLLECT : is_saved_in
    USER ||--o{ COMMENT : writes
    GOODS ||--o{ COMMENT : receives
    USER ||--o{ ORDERS : purchases
    USER ||--o{ ORDERS : sells
    ADDRESS ||--o{ ORDERS : fulfils
    GOODS ||--o{ ORDERS : becomes
    USER ||--o{ FEEDBACK : submits

    USER {
      int id PK
      string username
      string password
      string name
      string avatar
      string role
    }
    GOODS {
      int id PK
      int user_id FK
      string name
      decimal price
      string category
      string status
      string sale_status
      int read_count
    }
    ADDRESS {
      int id PK
      int user_id FK
      string name
      string phone
      string address
    }
    COLLECT {
      int id PK
      int user_id FK
      int fid FK
    }
    COMMENT {
      int id PK
      int user_id FK
      int fid FK
      int pid
      string module
      string content
    }
    ORDERS {
      int id PK
      int user_id FK
      int sale_id FK
      string order_no
      decimal total
      string status
    }
    FEEDBACK {
      int id PK
      int user_id FK
      string title
      string content
      string reply
      datetime createtime
    }
    NOTICE {
      int id PK
      string title
      string content
      datetime time
    }
```

The supplied SQL dump currently represents relationships with identifier
columns rather than declared database foreign-key constraints. This reduces
import friction but permits orphaned data. A production migration should add
foreign keys after existing seed data is validated.

### Status design

`goods.status` represents administrator approval. `goods.sale_status`
represents seller-controlled publication. They are deliberately separate:

| Concern | Values | Owner |
|---|---|---|
| Audit status | Pending, Approved, Rejected | Administrator |
| Publication status | `Listed`, `Off-shelf` | Listing owner |
| Order status | Pending Payment, To Ship, To Receive, Completed, Cancelled | Buyer/seller workflow |

Only approved and `Listed` goods are returned by the public marketplace query.
Legacy publication values are normalised at both UI and service boundaries.

## 5. Interface design

```mermaid
flowchart TD
    Login["Login / Register"] --> Home["Marketplace home"]
    Home --> Category["Browse by category"]
    Home --> Search["Keyword search"]
    Category --> Detail["Item details"]
    Search --> Detail
    Detail --> Message["Comments / messaging"]
    Detail --> Favourite["Save favourite"]
    Detail --> Buy["Create order"]
    Home --> Publish["Publish item"]
    Publish --> Listings["My listings"]
    Listings --> Status["On-shelf / Off-shelf"]
    Buy --> Orders["My purchases / My sales"]
    Home --> Profile["Profile"]
    Home --> Notice["Announcements"]
    Home --> Feedback["Feedback + reply history"]
```

### Interface principles

- Element UI provides consistent form, table, dialog and feedback patterns.
- Resident and administrator routes share visual components but keep tasks
  separated.
- Empty searches resolve to an empty list and zero total rather than an
  undefined collection.
- Publication controls show friendly labels while sending canonical values.
- Destructive actions require confirmation.
- Important transaction states are visible in the order list.

The repository contains the implemented interface rather than an external
interactive prototype. An assessor-facing prototype made with the required
online prototyping tool remains an explicit owner action in Issue #27.

## 6. Key decisions and trade-offs

| Decision | Benefit | Trade-off / follow-up |
|---|---|---|
| Vue 2 + Element UI | Fast implementation and consistent widgets | Older ecosystem; plan a future Vue 3 migration |
| Spring Boot layered API | Familiar separation and testable controllers/services | Some legacy services still rely on static request context |
| MyBatis | SQL remains visible and tuneable | Relationships and validation require discipline |
| JWT signed with account password | Tokens invalidate after a password change | Production should use a dedicated rotated signing secret |
| Local file storage | Simple coursework setup | Production requires durable object storage |
| Environment-based secrets | Current commits contain no live credentials | Previously exposed values must be revoked and history reviewed |
