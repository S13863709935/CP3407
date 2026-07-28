# Local Community Second-hand Marketplace

[![Quality CI](https://github.com/S13863709935/CP3407/actions/workflows/quality-ci.yml/badge.svg?branch=Backend)](https://github.com/S13863709935/CP3407/actions/workflows/quality-ci.yml)

A Vue and Spring Boot marketplace where residents publish, discover, discuss,
save and purchase second-hand items within a local community.

## Project evidence

The [assessment evidence hub](docs/index.md) connects every rubric category to
maintained project evidence:

- [Requirements, priorities, estimates and acceptance criteria](docs/requirements.md)
- [Architecture, database and interface design](docs/design.md)
- [Implementation evidence by iteration](docs/implementation.md)
- [Testing strategy, matrix and verified results](docs/testing.md)
- [Building and development tools](docs/tools.md)
- [Agile engineering record](docs/agile.md)
- [Deployment and operations](docs/deployment.md)
- [Security policy](SECURITY.md)
- [Contribution and review workflow](CONTRIBUTING.md)

## Delivered scope

### Iteration 1 — marketplace prototype

- User registration and login
- Item publishing with images
- Category browsing
- Item details and messaging

### Iteration 2 — discovery and transactions

- Keyword search
- On-shelf/off-shelf publication control
- Seller listing management
- Buyer and seller order workflow
- Profile customisation

### Iteration 3 — retention and communication

- Favourite items
- System announcements
- Feedback submission and administrator replies

## Technology

- Backend branch: Java 8, Spring Boot 2.5, MyBatis, MySQL, JWT, WebSocket
- Frontend branch: Vue 2, Vue Router, Axios, Element UI, ECharts, WangEditor
- Quality: JUnit 5, Mockito, JaCoCo, Node test runner, GitHub Actions

## Quick verification

Backend:

```bash
mvn clean verify
```

Frontend branch:

```bash
npm ci
npm test
npm run build
```

Runtime credentials are environment variables. Copy the relevant
`.env.example` values into an ignored local environment or IDE run
configuration; never commit real values.

## Repository structure

The repository intentionally maintains the existing coursework layout:

- `Backend` — Spring Boot API, SQL seed, tests and evidence documentation
- `Frontend` — Vue single-page application and frontend contract tests

Planning and quality work is tracked through the
[Quality & Evidence Sprint](https://github.com/S13863709935/CP3407/milestone/1).
