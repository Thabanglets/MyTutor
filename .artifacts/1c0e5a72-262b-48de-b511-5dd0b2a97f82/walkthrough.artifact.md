# Walkthrough - Secure Password Hashing & Authentication

Implemented secure SHA-256 password hashing across the SQLite database (`DatabaseHelper`), ensuring all preloaded users, new sign-ups, login checks, and profile updates use hashed passwords instead of plaintext strings.

## Changes

### Database & Authentication

#### [DatabaseHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/DatabaseHelper.kt)
- Added `hashPassword(password: String): String` utilizing Java `MessageDigest` (SHA-256).
- Updated `insertUserIfNotExists` and `addUser` to store hashed passwords.
- Updated `checkUser` to hash input credentials before querying the database.
- Updated `updateUser` to hash new passwords when modified via Profile settings.
- Incremented `DATABASE_VERSION` to `13` to trigger database upgrade and re-seed with hashed credentials.

## Verification Results

### Automated Tests
- Gradle build (`app:assembleDebug`) completed successfully.
- Unit tests (`app:testDebugUnitTest`) passed successfully (6 tests passed, 0 failures).

> [!NOTE]
> All preloaded users (Admin, Tutors, Students, Pending Users) and newly registered users now store secure SHA-256 hashes in the local SQLite database (`MyTutor.db`), preventing passwords from appearing in plaintext.
