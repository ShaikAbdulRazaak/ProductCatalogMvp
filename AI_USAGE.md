# AI-Assisted Development

## How AI was used

AI assistance was used as a pair-programming tool for:

- Exploring the Android and backend project structure.
- Implementing and refining Compose screens and ViewModels.
- Improving theme consistency, dimensions, strings, validation, and navigation.
- Reviewing the challenge acceptance criteria.
- Adding and updating unit and instrumentation tests.
- Improving BFF validation, upstream error handling, and runtime configuration.
- Producing setup and architecture documentation.

## Review process

Changes were reviewed against the existing architecture and tested incrementally. Verification included:

- Kotlin unit tests.
- Android instrumentation tests.
- Debug and release APK builds.
- TypeScript compilation.
- Docker Compose startup.
- HTTP smoke tests for every BFF endpoint.
- Installation and launch on an Android Emulator.

## Corrections made during development

Several implementation issues were found and corrected through testing:

- Category state was moved into the Home ViewModel so it survives navigation.
- A JVM-safe email validation expression replaced Android-only validation in unit-tested code.
- Instrumentation fixtures were expanded to cover the additional category section.
- A product-detail test was updated for the fixed bottom action.
- Backend enquiry validation was added so malformed submissions cannot return success.
- Upstream HTTP failures are now detected instead of being treated as successful JSON responses.

## Current limitations

- Enquiries are logged by a console stub and are not emailed.
- The backend uses Fake Store API as its upstream data source.
- No production authentication, persistence, payments, or order management is included.
- AWS deployment is documented but not required for local execution.

Human review remains responsible for product decisions, credentials, deployment configuration, and final acceptance.
