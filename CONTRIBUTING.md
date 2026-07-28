# Contributing

## Workflow

1. Select or create a GitHub Issue with acceptance criteria.
2. Create a branch such as `feature/issue-24-acceptance-tests`.
3. Add or update a test before changing behaviour where practical.
4. Keep commits focused and use `type: summary`, for example:
   - `feat: add listing publication control`
   - `fix: handle empty search results`
   - `test: cover order status transitions`
   - `docs: record iteration review`
5. Run verification locally.
6. Push the branch and open a Pull Request linked to the Issue.
7. Merge only after CI passes and review comments are resolved.

## Required checks

Backend:

```bash
mvn clean verify
```

Frontend:

```bash
npm ci
npm test
npm run build
```

## Repository hygiene

- Never commit credentials or private keys.
- Do not commit `target`, `dist`, `node_modules`, IDE state, logs, or local
  environment files.
- Update requirements, design, testing or deployment evidence when a change
  affects them.
- Do not claim client acceptance without a dated genuine review.
