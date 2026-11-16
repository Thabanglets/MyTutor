<div align="center">

# 🎓 MyTutor – Advanced Academic Tutoring & Management System

[![Android Unit Tests](https://github.com/your-username/MyTutor_Android/actions/workflows/blank.yml/badge.svg)](https://github.com/your-username/MyTutor_Android/actions/workflows/blank.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://www.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-27%20(Android%208.0)-blue.svg)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange.svg)](https://developer.android.com/reference/android/os/Build.VERSION_CODES#R)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)

*Learn • Connect • Grow*

</div>

---

## 📖 Table of Contents
1. [App Purpose & Overview](#-app-purpose--overview)
2. [Design Considerations](#-design-considerations)
3. [GitHub & GitHub Actions Utilisation](#-github--github-actions-utilisation)
4. [Release Notes & Innovative Features](#-release-notes--innovative-features)
5. [Installation & Setup](#-installation--setup)
6. [Pre-populated Seed Credentials](#-pre-populated-seed-credentials)
7. [Testing Suite](#-testing-suite)

---

## 🎯 App Purpose & Overview

**MyTutor** is a comprehensive, multi-platform educational management and tutoring ecosystem designed for students, educators (tutors), and administrators. It bridges the gap between learners seeking specialized academic assistance and qualified subject experts across various regions (e.g., Cape Town, Johannesburg, Durban, and Online).

### Core Objectives:
- **For Students:** Seamlessly discover vetted tutors filtered by subject and location, book interactive sessions, track academic progress, and generate attendance and performance reports.
- **For Tutors:** Efficiently manage lesson schedules, record session attendance, grade student submissions, and monitor teaching hours and engagement metrics.
- **For Administrators:** Oversee platform security, review and approve pending user registrations (students and tutors), and audit system-wide tutoring sessions.

---

## 📐 Design Considerations

The MyTutor application was architected following modern Android engineering best practices, emphasizing reliability, security, offline resilience, and fluid user experience.

### 1. Architecture & Data Persistence
- **Hybrid Sync Architecture:** Combines a local **SQLite** database (`MyTutor.db`) for instant offline-first reads and performance with **Firebase Realtime Database** (`/ayi`) for real-time multi-client synchronization (sharing state with the web admin dashboard).
- **Repository & Helper Pattern:** Centralized data access through `DatabaseHelper`, encapsulating SQLite queries and Firebase listeners.

### 2. Authentication & Security
- **Firebase Authentication:** Secure email/password and **Google Single Sign-On (SSO)** authentication workflows.
- **Role-Based Access Control (RBAC):** Distinct interfaces and privileges for Students, Tutors, and Administrators. Account approval gates (`Pending` vs `Approved`) ensure unauthorized users cannot access operational dashboards.
- **Credential Protection:** Passwords are securely hashed (SHA-256) locally and authenticated via Firebase Auth tokens; raw passwords are never mirrored to the database.

### 3. UI/UX Design System
- **Material Design 3 (M3):** Consistent use of rounded card containers (`CardView`), outlined text input layouts, and branded color palettes (`#6200EE` brand purple, `#1A202C` navy text).
- **Edge-to-Edge Support & Insets:** Responsive layouts adapting to system bars and cutouts across diverse phone and tablet form factors.

---

## 🔄 GitHub & GitHub Actions Utilisation

GitHub serves as the central version control and collaboration hub for the MyTutor project, ensuring code quality, transparent versioning, and automated testing.

### GitHub Repository Practices:
- **Branching Strategy:** Feature branches (`feature/sso`, `feature/auth-sync`) merged via Pull Requests into `master` after peer review and automated testing.
- **Semantic Versioning & Release Tags:** Tagged releases marking major milestones from prototype to production-ready builds.

### CI/CD Pipeline (GitHub Actions):
The project utilizes GitHub Actions (`.github/workflows/blank.yml`) to automate continuous integration and testing on every push and pull request to the `master` branch:
- **Automated Test Runner:** Provisions an `ubuntu-latest` runner with JDK 17, grants execution permissions to `./gradlew`, and executes targeted JVM unit test suites (`./gradlew testDebugUnitTest --tests "*UserBookingModelTest"`).
- **Concurrency Control:** Automatically cancels obsolete pipeline runs on the same branch to save CI minutes and provide rapid feedback.
- **Build Verification:** Ensures no breaking changes are introduced to data models or business logic prior to merge.

---

## 🚀 Release Notes & Innovative Features

### Version 1.0.0 (Current Production Release)

Since the initial prototype phase, MyTutor has evolved into a robust, secure, and feature-rich production application. Below is a comprehensive breakdown of updates and innovative features added:

| Version | Feature / Update | Description & Innovation |
| :--- | :--- | :--- |
| **v1.0.0** | **Google Single Sign-On (SSO)** | **[Innovative Feature]** Integrates Google Identity / Credential Manager via `GoogleAuthProvider` and `play-services-auth`, enabling 1-tap authentication with automatic profile provisioning in Firebase and SQLite. |
| **v1.0.0** | **Firebase Auth & Realtime DB Sync** | Migrated user credential management to Firebase Auth while keeping a real-time bi-directional listener sync (`/ayi/users`, `/ayi/students`, `/ayi/tutors`, `/ayi/sessions`) for seamless cross-client parity. |
| **v1.0.0** | **Admin Approval Workflow** | Implemented `Pending` / `Approved` status governance. New user sign-ups require administrative review before granting dashboard access. |
| **v1.0.0** | **Nylas Calendar Integration** | Added calendar helper integration (`NylasCalendarHelper.kt`) for advanced scheduling synchronization and calendar event management. |
| **v1.0.0** | **Robust Test Suite** | Added comprehensive unit tests (`UserBookingModelTest.kt`), instrumented SQLite integration tests (`DatabaseHelperTest.kt`), and Espresso UI tests (`LoginActivityTest.kt`). |
| **v1.0.0** | **Advanced Tutor Filtering** | Added multi-criteria filtering for students to search tutors by geographical location (Cape Town, Johannesburg, Durban, Online) and specific academic subjects. |
| **v1.0.0** | **Material 3 UI Refinement** | Polished visual styling across all screens with edge-to-edge support, custom bottom navigation, and dynamic statistics reporting. |

---

## 📥 Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/MyTutor_Android.git
   cd MyTutor_Android
   ```
2. **Open in Android Studio:**
   - Open Android Studio (Jellyfish or newer).
   - Select **Open** and choose the `MyTutor_Android` directory.
   - Allow Gradle sync to complete.
3. **Build & Run:**
   - Connect an Android device (API 27+) or start an AVD emulator.
   - Run the app via Android Studio or execute:
     ```bash
     ./gradlew app:assembleDebug
     ```

---

## 🔑 Pre-populated Seed Credentials

The app automatically seeds test accounts on first launch for immediate evaluation:

| Role | Full Name | Email Address | Password | Location / Specialty |
| :--- | :--- | :--- | :--- | :--- |
| 📚 **Tutor** | Dr. John Smith | `sam@gmail.com` | `simon@10111` | Cape Town (Advanced Mathematics) |
| 🎓 **Student** | Alex Johnson | `alex.johnson@gmail.com` | `password123` | Cape Town |

---

## 🧪 Testing Suite

MyTutor maintains high test coverage across all layers of the application:
- **JVM Unit Tests:** Verify data models, nullability, and validation rules (`UserBookingModelTest`).
  ```bash
  ./gradlew app:testDebugUnitTest
  ```
- **Instrumented Integration Tests:** Validate SQLite CRUD operations and database helper logic (`DatabaseHelperTest`).
- **UI Tests:** Verify layout rendering and user interaction flows (`LoginActivityTest`).

---
*© 2026 MyTutor Android Project. All rights reserved.*
