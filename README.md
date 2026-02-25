# ClimaApp

Android weather app with city search, 5-day forecast, favorites, and geolocation.

## 📱 Overview

**ClimaApp** is a clean, fast weather app for Android that shows current weather and forecasts for your location or any city worldwide.

### Features
- 🌍 **Current Weather**: Auto-detect location and display current conditions
- 📅 **5-Day Forecast**: Daily min/max temps with weather icons
- 🔍 **City Search**: Search any city with autocomplete
- ⭐ **Favorites**: Save up to 10 cities for quick access
- ⚙️ **Settings**: Customize units (°C/°F, km/h/mph)

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin 1.9.23 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Network | Retrofit + OkHttp |
| Location | FusedLocationProvider |
| Async | Coroutines + Flow |
| API | [Open-Meteo](https://open-meteo.com) (free, no key) |

## 📦 Module Structure

```
app/
├── data/           # Data sources, repositories, DTOs
├── domain/         # Use cases, models, interfaces
├── presentation/   # UI (Compose), ViewModels, navigation
└── di/             # Hilt modules
```

## 🚀 Build & Run

### Prerequisites
- Android Studio Hedgehog+ or command-line tools
- JDK 21
- Android SDK with API 34

### Build APK
```bash
ANDROID_HOME=~/android-sdk ./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Install on Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 🌐 API

**Open-Meteo** (https://open-meteo.com)
- **Weather API**: Current conditions + 5-day forecast
- **Geocoding API**: City search with autocomplete
- **No authentication required**
- **Free tier**: Unlimited requests

### Endpoints
- Forecast: `https://api.open-meteo.com/v1/forecast`
- Geocoding: `https://geocoding-api.open-meteo.com/v1/search`

## 📊 Project Status

**Current Phase:** Development (Sprint 3 — Favorites)

**Progress:** 55/117 pts (47% of MVP) | **Velocity:** ~10 pts/hour

### Sprint Progress
- **Sprint 1 — Foundation** (29/29 pts) ✅ **COMPLETE**
  - ✅ S1.1: Initialize Android Project
  - ✅ S1.2: Setup Hilt DI
  - ✅ S1.3: Setup Base Architecture
  - ✅ S2.1: Implement Location Provider
  - ✅ S2.2: Integrate Open-Meteo API
  
- **Sprint 2 — Weather Display** (26/26 pts) ✅ **COMPLETE**
  - ✅ S2.3: Build Weather Screen UI
  - ✅ S3.1: Implement City Search API
  - ✅ S3.2: Build Search Screen UI (7/7 ACs verified on device)

- **Sprint 3 — Favorites** (0/23 pts) 🔄 **NEXT**
  - ⏳ S4.1: Implement Favorites Repository
  - ⏳ S4.2: Build Favorites Screen UI
  - ⏳ S4.3: Add to Favorites from Weather Screen
  - ⏳ S4.4: Remove from Favorites

### Milestones
- ✅ [Sprint 1 — Foundation](https://github.com/giruai/clima-test/milestone/1) (29 pts) — CLOSED
- ✅ [Sprint 2 — Weather Display](https://github.com/giruai/clima-test/milestone/2) (26 pts) — CLOSED
- 🔄 [Sprint 3 — Favorites](https://github.com/giruai/clima-test/milestone/3) (23 pts)
- [Sprint 4 — Settings & Polish](https://github.com/giruai/clima-test/milestone/4) (21 pts)
- [Sprint 5 — Launch](https://github.com/giruai/clima-test/milestone/5) (18 pts)

[View all issues →](https://github.com/giruai/clima-test/issues)

### Recent Activity
- **2026-02-25**: Sprint 2 complete — Search Screen UI with autocomplete, recent searches, navigation (PR #27)
- **2026-02-25**: 3 critical bugs fixed during device testing (PermissionHandler crash, location fallback, nav args)
- **2026-02-24**: Sprint 1 complete — Weather Screen UI with location & API integration (PR #24)

## 🧪 Testing

- **Unit tests**: Domain layer (use cases, models)
- **Instrumented tests**: Room DAO operations
- **Device testing**: Mandatory QA on Moto G60s before merge

## 📋 Requirements

- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 34 (Android 14)
- **Permissions**:
  - `ACCESS_FINE_LOCATION` (runtime)
  - `INTERNET` (granted by default)

## 📄 License

(TBD)

## 👤 Author

Built by **Agente ClimaApp** (AI agent)  
Product Owner: Franco (@noscr33n)

---

**Last updated:** 2026-02-25
