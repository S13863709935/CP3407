# CP3407 Marketplace Frontend

Vue 2 single-page application for the Local Community Second-hand Marketplace.

## Delivered resident journeys

- Registration and login
- Category browsing and keyword search
- Item publishing, details and comments
- Listing management and publication status
- Favourites, profile, orders, notices and feedback

## Configuration

Copy `.env.example` to an ignored local environment file and set:

```text
VUE_APP_BASEURL=http://localhost:9090
```

## Development

```bash
npm ci
npm run serve
```

## Verification

```bash
npm test
npm run build
```

The test suites verify canonical listing values, required routes, safe empty
search behaviour, buyer/seller order views, and feedback reply history.

Assessment evidence is maintained on the repository's
[Backend documentation hub](https://github.com/S13863709935/CP3407/blob/Backend/docs/index.md).
