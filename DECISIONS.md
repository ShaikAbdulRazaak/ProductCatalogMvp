# Architecture and Product Decisions

## Inline category browsing

Categories are displayed on the Home screen instead of opening a separate category route. This keeps the browsing context visible and allows the selected category to remain active when the user returns from product details or an enquiry.

## Category acceptance criterion

The category experience shows products in two groups:

1. Top-rated products in the selected category.
2. Additional products from that category.

Products are sorted by rating and up to two products are promoted into the top-rated group. Remaining products are shown below.

## Backend-for-frontend

The Android app talks to the Node.js BFF rather than directly to Fake Store API. This keeps upstream details out of the mobile client and provides one place for filtering, validation, error handling, and future integrations.

## Enquiry submission

The BFF currently logs valid enquiries through a clearly labelled console stub. Real email delivery is intentionally deferred because it requires a provider account, verified sender identity, credentials, and deployment configuration. Amazon SES is the planned production-compatible option.

## Local networking

The Android debug build defaults to `10.0.2.2`, which maps from the Android Emulator to the host machine. `API_BASE_URL` can be supplied as a Gradle property for a physical device or deployed backend.

## Runtime configuration

The BFF reads `PORT` and `FAKE_STORE_API` from its environment. Android reads `API_BASE_URL` from Gradle configuration and does not require source changes when the backend address changes.

## AWS deployment

AWS App Runner is the preferred future deployment target because it can run the existing Dockerized Express service without a serverless rewrite. AWS is not required for the local challenge demonstration and should only be enabled with a billing budget and a cleanup plan.

## Scope boundaries

The POC does not add authentication, payments, persistent orders, a database, wishlist behavior, or real email delivery. These are intentionally outside the current product catalogue and enquiry acceptance scope.

## If more time were available

- Add backend integration tests and a health endpoint.
- Add persistent enquiry storage and an email provider.
- Add environment-specific Android build variants.
- Add automated Docker image publishing and deployment.
- Add accessibility and network-failure test coverage.
