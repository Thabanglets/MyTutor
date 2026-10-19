# Walkthrough - Integrate Nylas API for Calendar Scheduling

Successfully replaced the Google Calendar OAuth implementation with the **Nylas API**, allowing calendar event scheduling using simple API keys.

## Changes

### Calendar Helper
#### [NEW] [NylasCalendarHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/NylasCalendarHelper.kt)
- Created `NylasCalendarHelper` object utilizing Nylas API v3 REST endpoint (`POST /v3/grants/{grant_id}/events`) with HTTP authorization headers (`Authorization: Bearer <NYLAS_API_KEY>`).
- Added fallback support to native device calendar (`CalendarContract`) if API calls fail.

### UI Fragments
#### [MODIFY] [BookingFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/BookingFragment.kt)
- Replaced Google Sign-In intent and OAuth permissions flow with direct asynchronous calls to `NylasCalendarHelper.insertEventToNylas`.

#### [MODIFY] [TutorScheduleFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/TutorScheduleFragment.kt)
- Replaced Google Sign-In and OAuth flow with direct asynchronous calls to `NylasCalendarHelper.insertEventToNylas`.

### Clean Up
#### [DELETE] `GoogleCalendarHelper.kt`
- Removed obsolete Google Calendar OAuth helper.
#### [MODIFY] [build.gradle.kts](file:///C:/Users/choki/StudioProjects/MyTutor/app/build.gradle.kts)
- Removed unused Google API Client and Google Calendar API dependencies.

## Verification Results

### Automated Tests
- Ran `gradle_build("app:assembleDebug")` successfully. Build passed with zero errors.
