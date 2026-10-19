package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

object NylasCalendarHelper {

    private const val TAG = "NylasCalendarHelper"

    // Replace these with your Nylas API Key and Grant ID
    private const val NYLAS_API_KEY = "nyk_v0_t7IGTfBrfTnPg1J9dieexsfD5SP8hi5Vyq3PA4y7o7tNEnc8FnnbsaYr1erBCNq0"
    // NOTE: When using a Nylas API key (nyk_v0_...), you must provide your actual Grant ID from the Nylas Dashboard (e.g. "12345678-abcd-..."), NOT "me".
    private const val NYLAS_GRANT_ID = "796b8261-81ee-47f1-9997-965f355d610b"
    private const val NYLAS_API_BASE_URL = "https://api.us.nylas.com/v3"

    /**
     * Parse date string ("dd/MM/yyyy") and time string ("HH:mm") into java.util.Calendar.
     */
    fun parseDateTime(dateStr: String, timeStr: String): Calendar {
        val calendar = Calendar.getInstance()
        try {
            val dateParts = dateStr.split("/")
            if (dateParts.size == 3) {
                val day = dateParts[0].toInt()
                val month = dateParts[1].toInt() - 1
                val year = dateParts[2].toInt()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            }

            val timeParts = timeStr.split(":")
            if (timeParts.size == 2) {
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date/time: ${e.message}")
        }
        return calendar
    }

    /**
     * Creates an Intent to pre-fill the native device Calendar app via CalendarContract as fallback.
     */
    fun createCalendarIntent(
        subject: String,
        tutorOrStudentName: String,
        dateStr: String,
        timeStr: String,
        durationHours: Int = 1
    ): Intent {
        val startCal = parseDateTime(dateStr, timeStr)
        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.HOUR_OF_DAY, durationHours)

        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "$subject Tutoring Lesson")
            putExtra(CalendarContract.Events.DESCRIPTION, "MyTutor lesson with $tutorOrStudentName")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "MyTutor Online Session")
        }
    }

    /**
     * Inserts an event directly into Nylas Calendar via Nylas API v3 REST endpoint.
     */
    suspend fun insertEventToNylas(
        title: String,
        description: String,
        dateStr: String,
        timeStr: String,
        durationHours: Int = 1
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val startCal = parseDateTime(dateStr, timeStr)
            val endCal = startCal.clone() as Calendar
            endCal.add(Calendar.HOUR_OF_DAY, durationHours)

            val url = URL("$NYLAS_API_BASE_URL/grants/$NYLAS_GRANT_ID/events?calendar_id=primary")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $NYLAS_API_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject().apply {
                put("title", title)
                put("description", description)
                put("when", JSONObject().apply {
                    put("start_time", startCal.timeInMillis / 1000)
                    put("end_time", endCal.timeInMillis / 1000)
                })
                put("location", "MyTutor Online Session")
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseStr = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    responseStr.append(line)
                }
                reader.close()
                Log.d(TAG, "Successfully created Nylas event: $responseStr")
                Result.success("Event created successfully via Nylas")
            } else {
                val reader = BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream))
                val errorStr = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    errorStr.append(line)
                }
                reader.close()
                Log.e(TAG, "Failed to insert event into Nylas: $responseCode - $errorStr")
                Result.failure(Exception("Nylas API error: $responseCode"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception inserting event into Nylas: ${e.message}", e)
            Result.failure(e)
        }
    }
}
