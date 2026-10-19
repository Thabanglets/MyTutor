# Integrate Nylas API for Calendar Event Scheduling

Replace Google Calendar OAuth sign-in flow with Nylas API v3, which uses a simple API key and Grant ID for seamless calendar event scheduling.

## User Review Required

> [!IMPORTANT]
> **API Key & Grant ID Configuration**: To use the Nylas API, you will need a Nylas Developer Account (free tier), an API Key, and a Calendar Grant ID for the user's calendar. We will provide placeholders for these keys in `NylasCalendarHelper.kt` or `local.properties`.

## Open Questions

- Should we completely replace `GoogleCalendarHelper.kt` or keep it alongside `NylasCalendarHelper.kt` as a fallback? (Proposed: Replace/upgrade `GoogleCalendarHelper` usage with `NylasCalendarHelper` while keeping native calendar intent fallback).
- Should we use OkHttp / coroutines for making the Nylas API POST request? (Proposed: Yes, using lightweight OkHttp or standard networking to avoid heavy SDK bloat).

## Proposed Changes

### Calendar Helper

#### [NEW] [NylasCalendarHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/NylasCalendarHelper.kt)
- Create `NylasCalendarHelper` object to handle event creation via Nylas API v3 REST endpoint (`POST /v3/grants/{grant_id}/events`).
- Uses API key authorization header and parses dates/times into Nylas event payload format (startTime, endTime, title, description, location).

#### [MODIFY] [BookingFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/BookingFragment.kt)
- Replace Google Sign-In launcher flow when "Google Calendar" toggle is checked with direct call to `NylasCalendarHelper.insertEvent(...)`.

#### [MODIFY] [TutorScheduleFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/TutorScheduleFragment.kt)
- Replace Google Sign-In launcher flow with direct call to `NylasCalendarHelper.insertEvent(...)`.

#### [DELETE] [GoogleCalendarHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/GoogleCalendarHelper.kt)
- Remove obsolete Google Calendar OAuth helper if fully replaced by Nylas API.

## Verification Plan

### Automated Tests
- Run unit tests (`gradle_build("app:testDebugUnitTest")`) to ensure compilation and existing unit tests pass.
- Run UI tests if applicable.

### Manual Verification
- Deploy app to emulator/device.
- Book a session with Nylas Calendar sync enabled.
- Verify event is successfully pushed to Nylas Calendar via API.
