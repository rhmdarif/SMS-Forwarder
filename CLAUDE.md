# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Aturan AI Agent

1. **Bahasa**: Selalu menjawab dan berkomunikasi dalam Bahasa Indonesia
2. **Klarifikasi**: Selalu berdiskusi dan bertanya jika terdapat ketidakjelasan informasi sebelum melakukan implementasi

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "id.majopay.gateway.ClassName"

# Run lint checks
./gradlew lint

# Clean build
./gradlew clean
```

## Project Overview

Majopay Gateway is an Android app that monitors SMS messages and app notifications, forwarding matching content to HTTP endpoints based on user-defined rules. It uses pattern matching (regex or substring) to filter messages.

**Tech Stack**: Kotlin, Jetpack Compose, Room, Hilt, Retrofit, WorkManager, Coroutines/Flow

**SDK Targets**: minSdk 29 (Android 10), targetSdk 34 (Android 14), Java 11

## Architecture

Clean Architecture with MVVM pattern:

```
app/src/main/java/com/zerodev/smsforwarder/
├── data/           # Data layer: Room DB, Retrofit, BroadcastReceivers, Services
├── domain/         # Business logic: models, use cases
├── di/             # Hilt dependency injection modules
└── ui/             # Jetpack Compose screens and ViewModels
```

### Message Processing Flow

1. **SmsReceiver** (BroadcastReceiver) or **NotifRouterService** (NotificationListenerService) intercepts messages
2. **SmsForwardingService** (foreground service) handles processing
3. **SmsForwardingUseCase** matches rules and coordinates forwarding
4. **HttpClient** executes HTTP requests with exponential backoff retry (3 attempts)
5. **HistoryRepository** logs all attempts (matched and unmatched)

### Key Domain Models

- **Rule**: Forwarding rule with pattern, endpoint, HTTP method, headers, source type (SMS/NOTIFICATION)
- **ForwardingHistory**: Log entry for each message processed (success/failed/no match)
- **SmsMessage**: Incoming SMS with sender, body, timestamp

### Database

Room database (version 4) with two tables:
- `rules`: Forwarding rule configurations
- `history`: All message processing attempts with request/response data

Schema exports to `app/schemas/` directory.

## Testing

Unit tests use Mockito and coroutines-test. Test files are in `app/src/test/`. Room and WorkManager have dedicated testing dependencies configured.

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- **build-and-release.yml**: Builds APKs, runs security scans, creates releases on "release:" commits
- **pr-check.yml**: Lint, test, and build validation for PRs

Release pattern: Commit with message starting with `release:` triggers automatic release creation.

## Protected Permissions

The app requires special permissions that trigger lint warnings (intentionally disabled):
- `BIND_NOTIFICATION_LISTENER_SERVICE` - for notification monitoring
- `QUERY_ALL_PACKAGES` - for app picker UI
- `PACKAGE_USAGE_STATS` - for MRU app sorting
