# Changelog

All notable changes to the coursework release are recorded here.

## Unreleased - Quality & Evidence Sprint

### Security

- Replaced committed runtime credentials with environment variables.
- Added safe environment examples and security guidance.
- Removed generated build and IDE artifacts from version control.
- Added server-side ownership checks for listing update and delete actions.

### Fixed

- Standardised listing publication values across Vue, Spring Boot and MyBatis.
- Preserved safe empty-state behaviour for keyword searches.
- Preserved safe empty-state behaviour for order, favourite, and feedback
  lists.

### Testing

- Added deterministic backend tests mapped to the 12 delivered user stories.
- Added H2-backed MyBatis integration tests for public listing visibility.
- Added frontend status and UI contract suites.
- Added JaCoCo reporting and a combined GitHub Actions quality workflow.
- Expanded the backend suite from 39 to 90 tests across authentication,
  addresses, comments, chat, orders, profiles, favourites, announcements,
  feedback, and exception responses.
- Raised JaCoCo coverage from 31.5% to 50.1% of lines and from 26.8% to 50.9%
  of branches.

### Documentation

- Added requirements, design, implementation, testing, tools, agile and
  deployment evidence pages.
- Added contribution, security and pull-request guidance.
