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
./gradlew test --tests "id.majopay.ngateway.ClassName"

# Run lint checks
./gradlew lint

# Clean build
./gradlew clean
```

## Project Overview

Majopay Gateway is an Android app that monitors app notifications, forwarding matching content to HTTP endpoints based on user-defined rules. It uses pattern matching (regex or substring) to filter messages.

**Tech Stack**: Kotlin, Jetpack Compose, Room, Hilt, Retrofit, Coroutines/Flow

**SDK Targets**: minSdk 29 (Android 10), targetSdk 36 (Android 16), Java 11

## Architecture

Clean Architecture with MVVM pattern:

```
app/src/main/java/id/majopay/ngateway/
├── data/           # Data layer: Room DB, Retrofit, NotificationListenerService
├── domain/         # Business logic: models, use cases
├── di/             # Hilt dependency injection modules
└── ui/             # Jetpack Compose screens and ViewModels
```

### Message Processing Flow

1. **NotifRouterService** (NotificationListenerService) intercepts notifications. Service ini di-bind sistem dan tersambung ulang otomatis setelah reboot, jadi tidak ada foreground service, boot receiver, atau WorkManager.
2. **ForwardingUseCase** matches rules and coordinates forwarding
3. **HttpClient** executes HTTP requests with exponential backoff retry (3 attempts)
4. **HistoryRepository** logs all attempts (matched and unmatched)

### Key Domain Models

- **Rule**: Forwarding rule with pattern, package filter, source type (hanya NOTIFICATION)
- **ForwardingHistory**: Log entry for each message processed (success/failed/no match)

### Database

Room database (version 5) with two tables:
- `rules`: Forwarding rule configurations
- `forwarding_history`: All message processing attempts with request/response data

`MIGRATION_4_5` membuang baris bersumber SMS. Nama kelas `SmsForwarderDatabase` dan file `sms_forwarder_database` sengaja dipertahankan agar data pengguna lama dan direktori skema tetap cocok.

Schema exports to `app/schemas/` directory.

## Testing

Unit tests use Mockito and coroutines-test. Test files are in `app/src/test/`. Room has a dedicated testing dependency configured.

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- **build-and-release.yml**: Builds APKs, runs security scans, creates releases on "release:" commits
- **pr-check.yml**: Lint, test, and build validation for PRs

Release pattern: Commit with message starting with `release:` triggers automatic release creation.

## Protected Permissions & Google Play

- `BIND_NOTIFICATION_LISTENER_SERVICE` - for notification monitoring (lint `ProtectedPermissions` sengaja dimatikan)
- `QUERY_ALL_PACKAGES` dan `PACKAGE_USAGE_STATS` **sengaja tidak diminta** karena ditolak kebijakan Play untuk kasus app picker. Jangan ditambahkan kembali.
- Izin SMS (`RECEIVE_SMS`, `READ_SMS`) **dihapus** setelah ditolak Play pada 3 Okt 2026 (Policy Declaration for SMS/Call Log). Jangan ditambahkan kembali. Foreground service, `RECEIVE_BOOT_COMPLETED`, `WAKE_LOCK`, dan `POST_NOTIFICATIONS` ikut dihapus karena hanya melayani SMS. Menambahkannya lagi berarti wajib deklarasi baru di Play Console.
- Sebelum membuka akses notifikasi, UI wajib menampilkan `DataDisclosureDialog` (prominent disclosure).
- Dokumen kebijakan privasi dan checklist Play Console ada di `docs/play-store/`. URL kebijakan privasi diatur lewat `PRIVACY_POLICY_URL` di `local.properties`.
