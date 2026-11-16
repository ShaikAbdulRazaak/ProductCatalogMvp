# Architecture and Product Decisions

## Foundation-first POC

The challenge asks for a proof of concept that can grow rather than be thrown
away. The implementation therefore keeps the Android client, BFF, and
upstream product source separate. Reusable dimensions, strings, and other
design tokens are centralized so the UI can evolve without magic numbers or
duplicated copy. The BFF also provides a seam for replacing Fake Store API,
adding persistence, or introducing a real email provider later.

## AI-assisted delivery and time-box

The application and backend took approximately five hours. Gemini (website and
Android Studio plugin) was used for initial planning and backend education and
bootstrapping. ChatGPT (website) was used for the design theme and screenshot
reviews. Copilot in VS Code, using Copilot Fusion's automatic
use-case-based model selection, was used to implement and iterate on the
Android and backend code. Generated output was reviewed and validated by the
author throughout.

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

The backend was initially explored with Gemini-generated local code. AWS
deployment was then stalled after the pricing implications became clear. This
is an intentional scope decision: the challenge must not require reviewers to
create paid accounts, so Docker Compose and localhost are the supported
deployment for this submission. The container remains compatible with a later
AWS deployment.

## Scope boundaries

The POC does not add authentication, payments, persistent orders, a database, wishlist behavior, or real email delivery. These are intentionally outside the current product catalogue and enquiry acceptance scope.

## If more time were available

- Expand backend integration-test coverage beyond the current smoke tests.
- Add persistent enquiry storage and an email provider.
- Add environment-specific Android build variants.
- Add automated Docker image publishing and deployment.
- Add accessibility and network-failure test coverage.

The actual implementation already includes a health endpoint and backend
smoke-test coverage; the remaining next steps are the persistence, delivery,
deployment automation, and broader accessibility/network coverage listed
above.
