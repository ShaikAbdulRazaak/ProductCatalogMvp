# Product Catalog MVP

A Modern Heritage fashion ecommerce proof of concept built with Android Jetpack Compose and a local Node.js backend-for-frontend (BFF).

## Architecture

```text
Android app
  Presentation: Compose screens, navigation, ViewModels, StateFlow
  Domain: product models and repository contracts
  Data: Retrofit API, DTO mapping, repository implementation
        |
        v
Node.js BFF (Express, Docker)
        |
        v
Fake Store API
```

The Android app calls the BFF, not the Fake Store API directly. The BFF provides featured products, category products, search, product details, and enquiry submission.

## Requirements

- Android Studio with an Android Emulator
- JDK 21
- Docker Desktop
- Node.js 20 is included in the backend container

## Run locally

### 1. Start the BFF

From the repository root:

```powershell
cd Backend
docker compose up --build
```

The API is available at `http://localhost:3000`.

Useful endpoint checks:

```powershell
Invoke-WebRequest http://localhost:3000/api/products/featured
Invoke-WebRequest "http://localhost:3000/api/products/search?q=shirt"
```

The backend health check is available at `http://localhost:3000/health`. Run the complete smoke test suite with:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

Run that command from the `Backend` directory.
The suite covers endpoint availability, category and search responses, upstream missing-product handling, successful enquiries, and each enquiry validation rule.

### 2. Run Android

The default debug build uses the Android Emulator host alias:

```text
http://10.0.2.2:3000/api/
```

Build and install from the repository root:

```powershell
.\gradlew.bat :app:assembleDebug
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

For a physical device, provide the computer's LAN address:

```powershell
.\gradlew.bat :app:assembleDebug -PAPI_BASE_URL=http://192.168.1.6:3000/api/
```

The backend port must be reachable from the device, and Windows Firewall may need an inbound rule for port 3000.

## Features

- Featured product browsing
- Inline category browsing with selected-category persistence
- Top-rated and additional category product sections
- Product search
- Product details
- Product enquiry form with validation and loading/error states
- Light and dark Modern Heritage themes
- Responsive Compose layouts

## Validation

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Enquiry behavior

The BFF validates product ID, name, email, and message. Submission currently uses an explicit console stub rather than sending real email. Amazon SES is a possible future integration and requires verified identities and AWS configuration.

## AWS deployment

AWS is not required for the local POC. If a public HTTPS backend is needed later, the existing Docker image can be deployed to AWS App Runner through Amazon ECR. Configure a billing budget before creating paid resources.

## Project documentation

- [DECISIONS.md](./DECISIONS.md) records important product and architecture decisions.
- [AI_USAGE.md](./AI_USAGE.md) records AI-assisted development and review history.
