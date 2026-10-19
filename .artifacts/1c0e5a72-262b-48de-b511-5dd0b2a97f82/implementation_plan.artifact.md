# Implementation Plan - Secure Password Hashing & Authentication

Implement secure password hashing (SHA-256) across the SQLite database (`DatabaseHelper`), ensuring all preloaded users, new sign-ups (`SignUpActivity`), login checks (`LoginActivity`), and profile updates (`ProfileFragment` / `DatabaseHelper`) correctly hash and verify passwords.

## User Review Required

> [!IMPORTANT]
> Passwords in the SQLite database will now be stored as SHA-256 hashes. Existing preloaded accounts (admin, tutors, students, pending users) and newly registered users will have their passwords hashed upon insertion/seeding. Login authentication automatically hashes the entered password to match the stored hash.

## Proposed Changes

### Database & Authentication

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/DatabaseHelper.kt)
- Add private `hashPassword(password: String): String` method using SHA-256.
- Update `insertUserIfNotExists` to hash the password before inserting into ContentValues.
- Update `addUser` to hash the password before inserting.
- Update `checkUser` to hash the input password before querying/comparing.
- Update `updateUser` to hash the password if a new non-empty password is provided.

## Verification Plan

### Automated Tests
- Run `DatabaseHelperTest.kt` via Gradle task (`app:connectedAndroidTest` or unit tests) to verify:
  - User credentials check (`testCheckUserCredentials`)
  - User registration (`testAddAndCheckNewUser`)
  - Preloaded user retrieval and validation (`testPreloadedUsersCount`, `testGetUser`)

### Manual Verification
- Deploy app to emulator/device.
- Test login with preloaded credentials (e.g., `admin@gmail.com` / `admin123`, `johnsmith@gmail.com` / `password123`).
- Test signing up a new user and logging in with the newly created account.
