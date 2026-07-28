# Building and Development Tools

## 1. Tool selection

| Tool/library | How it is used | Why it fits this project |
|---|---|---|
| Git and GitHub | Branches, commits, Issues, milestone, PRs and releases | Connects planning decisions to reviewable implementation evidence |
| GitHub Actions | Backend/frontend CI and downloadable artifacts | Makes verification repeatable outside the developer laptop |
| Maven | Java dependency resolution, testing, packaging and JaCoCo lifecycle | Standard reproducible build for Spring Boot |
| Java 8 / Spring Boot 2.5 | REST controllers, services, configuration and WebSocket endpoint | Matches the supplied codebase and established ecosystem |
| MyBatis | Mapper interfaces and explicit SQL | Provides control over category, search and user-scoped queries |
| PageHelper | Pagination around MyBatis queries | Avoids hand-written offset/count handling in every service |
| MySQL | Persistent marketplace data | Appropriate relational model for users, orders, listings and ownership |
| JUnit 5 | Backend test lifecycle and assertions | Included with Spring Boot test support |
| Mockito | Isolated controller/service tests | Tests behaviour without requiring a real database |
| JaCoCo | HTML/XML coverage evidence | Provides a measurable baseline and exposes untested legacy areas |
| Vue 2 / Vue CLI | Single-page application and production bundling | Matches the existing UI while supporting route-based views |
| Vue Router | Resident and administrator navigation | Maps journeys to explicit, testable routes |
| Axios | HTTP API wrapper and token header | Centralises transport and authentication behaviour |
| Element UI | Forms, tables, dialogs, pagination and feedback | Delivers consistent UI within the project budget |
| WangEditor | Rich listing/post content | Avoids building a custom content editor |
| ECharts | Administrator order visualisation | Supports reporting with a mature chart library |
| Node test runner | Dependency-free frontend contract suites | Adds fast deterministic checks without changing the Vue 2 stack |
| Alipay SDK | Sandbox payment entry | Reuses the provider contract rather than implementing payment protocols |

## 2. Build automation

The Maven lifecycle is extended by JaCoCo:

```text
compile -> test -> package -> coverage report
```

The frontend uses the locked dependency graph:

```text
npm ci -> npm test -> npm run build
```

The `Quality CI` workflow executes both paths on GitHub-hosted runners and
uploads evidence artifacts. CI configuration is version controlled with the
application.

## 3. Configuration practice

Runtime values are supplied through environment variables:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `SERVER_PORT`, `APP_HOST`
- `ALIPAY_APP_ID`, `ALIPAY_APP_PRIVATE_KEY`
- `ALIPAY_PUBLIC_KEY`, `ALIPAY_NOTIFY_URL`
- `VUE_APP_BASEURL`

`.env.example` files document names without containing real secrets. Generated
build output and IDE settings are ignored.

## 4. Known tool debt

- Spring Boot 2.5 and Vue 2 are older supported baselines; future maintenance
  should plan upgrades rather than mixing them into the assessment release.
- The current frontend bundle is large. Dependency splitting and image
  optimisation are follow-up performance tasks.
- The SQL dump lacks migration tooling and declared foreign keys.
- A containerised local environment would reduce setup differences.
