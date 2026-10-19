# Nylas API Integration Guide - MyTutor App

> [!NOTE]
> This guide outlines the implementation, architecture, configuration, and testing of the Nylas API v3 calendar integration in the MyTutor Android application.

---

## 1. Executive Summary

To avoid the complex setup of Google OAuth 2.0 (which requires Google Cloud Console configuration, OAuth consent screens, and SHA-1 signing certificates), **MyTutor** integrates the **Nylas API v3**.

Nylas provides a unified calendar and email REST API accessible via a simple developer API key and Grant ID, enabling seamless calendar event synchronization across Google Calendar, Outlook, and Apple Calendar.

---

## 2. Architecture & Component Breakdown

```mermaid
graph TD
    A[Student/Tutor Booking UI] -->|Saves to SQLite| B[DatabaseHelper]
    A -->|Toggles Calendar Sync| C[NylasCalendarHelper]
    C -->|POST /v3/grants/{grant_id}/events| D[Nylas API v3]
    D -- Success (200 OK) --> E[Toast: Synced to Nylas Calendar]
    D -- Failure (Offline / Invalid Key) --> F[Native Calendar Fallback CalendarContract]
```

### Core Files Modified/Created:
1. **[NylasCalendarHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/NylasCalendarHelper.kt)**
   - Parses dates (`dd/MM/yyyy`) and times (`HH:mm`) into Unix Epoch timestamps (`start_time` and `end_time` in seconds).
   - Executes asynchronous network requests on `Dispatchers.IO` using `HttpURLConnection`.
   - Generates native `CalendarContract` fallback intents.
2. **[BookingFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/BookingFragment.kt)**
   - Student booking interface.
   - Triggers Nylas calendar sync upon successful session booking.
   - Manages safe coroutine execution and post-sync navigation.
3. **[TutorScheduleFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/TutorScheduleFragment.kt)**
   - Tutor scheduling interface.
   - Triggers Nylas calendar sync upon successful lesson scheduling.

---

## 3. Configuration & Credentials

To use the Nylas API in your app, configure your credentials in `NylasCalendarHelper.kt`:

```kotlin
object NylasCalendarHelper {
    private const val NYLAS_API_KEY = "nyk_v0_t7IGTfBrfTnPg1J9dieexsfD5SP8hi5Vyq3PA4y7o7tNEnc8FnnbsaYr1erBCNq0"
    private const val NYLAS_GRANT_ID = "YOUR_NYLAS_GRANT_ID_HERE" // Get from Nylas Dashboard
    private const val NYLAS_API_BASE_URL = "https://api.us.nylas.com/v3"
}
```

> [!IMPORTANT]
> **How to get your Grant ID:**
> 1. Log into your [Nylas Dashboard](https://dashboard.nylas.com/).
> 2. Create a test application.
> 3. Go to **Accounts / Grants** and connect a test calendar (Google or Outlook).
> 4. Copy the resulting **Grant ID** string and paste it into `NYLAS_GRANT_ID`.

---

## 4. API Request Specification

When a session is booked, the helper sends a `POST` request to Nylas:

* **Endpoint:** `POST https://api.us.nylas.com/v3/grants/{grant_id}/events?calendar_id=primary`
* **Headers:**
  * `Authorization: Bearer <NYLAS_API_KEY>`
  * `Content-Type: application/json`
* **Payload Format:**
  ```json
  {
    "title": "Mathematics Tutoring Lesson",
    "description": "MyTutor lesson with John Doe",
    "when": {
      "start_time": 1718710800,
      "end_time": 1718714400
    },
    "location": "MyTutor Online Session"
  }
  ```

---

## 5. Fallback Mechanism

If the device is offline, or if the API key/Grant ID is invalid, `NylasCalendarHelper` catches the exception and launches the device's **native Calendar app** (`Intent.ACTION_INSERT` with `CalendarContract.Events.CONTENT_URI`), ensuring the user's lesson is never lost.

---

## Exporting to PDF
*You can export this guide to PDF in Android Studio by opening this markdown file and clicking **Print** or **Export to PDF** (or by opening it in any Markdown viewer / browser and selecting "Print > Save as PDF").*
