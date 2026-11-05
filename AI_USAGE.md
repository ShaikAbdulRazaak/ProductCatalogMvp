# AI-Assisted Development

## Tools and models

This project was built with several AI tools, each used for a different part of
the workflow:

- **Gemini website:** initial planning, backend concepts, and explanations of
  what to build when the backend requirements were not yet clear.
- **Gemini Android Studio plugin:** planning and implementation guidance while
  working in the Android project.
- **ChatGPT website:** creation of the Modern Heritage design theme, followed
  by screenshot-based design review and suggestions for visual improvements.
- **GitHub Copilot in VS Code:** implementation, refactoring, testing, and
  documentation across the Android app and Node.js BFF. Copilot Fusion was
  selected; it automatically picks the model best suited to each use case.

No agent instruction files or other AI configuration files were committed.

## Plan and key prompts or moments

The initial plan was to make the POC a foundation that could evolve beyond the
challenge. That led to clear client/BFF/data-source boundaries and reusable
structured dimensions, strings, and other design tokens instead of scattered
magic numbers or hardcoded copy.

Key prompts and moments that shaped the solution included:

1. Asking Gemini to explain the backend requirement and generate a local
   backend from the acceptance criteria when there was no existing backend
   plan.
2. Asking Gemini how to deploy the backend to AWS, then pausing that route
   after it identified that AWS pricing could be a risk for a no-paid-service
   submission.
3. Asking ChatGPT to create a Modern Heritage design theme for the app.
4. Sharing screenshots of the implementation with ChatGPT to identify design
   differences and prioritize visual improvements.
5. Asking Copilot in VS Code to implement the app and BFF, then reviewing the
   resulting code and iterating when a screen or behavior differed slightly
   from the intended design.

The work took approximately five hours: about one hour of planning and
architecture, two hours and 45 minutes of implementation, and one hour and
15 minutes of testing, screenshot review, iteration, and documentation.

## Review process

Changes were reviewed against the existing architecture and tested incrementally. Verification included:

- Kotlin unit tests.
- Android instrumentation tests.
- Debug and release APK builds.
- TypeScript compilation.
- Docker Compose startup.
- HTTP smoke tests for every BFF endpoint.
- Installation and launch on an Android Emulator.

The AI-generated output was treated as a draft. I read through the code as it
progressed, checked behavior against the challenge PDF, used screenshots to
review the UI, and approved or requested iterations when the implementation
did not match the intended design.

## Corrections made during development

Several implementation issues were found and corrected through testing:

- Category state was moved into the Home ViewModel so it survives navigation.
- A JVM-safe email validation expression replaced Android-only validation in unit-tested code.
- Instrumentation fixtures were expanded to cover the additional category section.
- A product-detail test was updated for the fixed bottom action.
- Backend enquiry validation was added so malformed submissions cannot return success.
- Upstream HTTP failures are now detected instead of being treated as successful JSON responses.

The initial backend direction also assumed that a cloud deployment could be
added immediately. After reviewing the AWS implications and potential cost, it
was corrected to a local Docker-only backend for this POC. This keeps the
reviewer setup free and preserves a deployable container for future work.

## Current limitations

- Enquiries are logged by a console stub and are not emailed.
- The backend uses Fake Store API as its upstream data source.
- No production authentication, persistence, payments, or order management is included.
- AWS deployment is documented but not required for local execution.

Human review remains responsible for product decisions, credentials, deployment configuration, and final acceptance.
