package com.example.mytutor

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = DatabaseHelper(context)
    }

    @After
    fun tearDown() {
        dbHelper.closeDatabase()
    }

    @Test
    fun testPreloadedUsersCount() {
        val tutors = dbHelper.getAllTutorsWithBio()
        val students = dbHelper.getAllStudents()
        val pending = dbHelper.getPendingUsers()

        assertTrue("Expected at least 8 approved tutors preloaded", tutors.size >= 8)
        assertTrue("Expected at least 6 approved students preloaded", students.size >= 6)
        assertTrue("Expected pending users preloaded", pending.isNotEmpty())
    }

    @Test
    fun testPreloadedBookingsCount() {
        val sessions = dbHelper.getAllSessions()
        assertTrue("Expected at least 10 session bookings preloaded", sessions.size >= 10)
    }

    @Test
    fun testCheckUserCredentials() {
        val isValidAdmin = dbHelper.checkUser("admin@gmail.com", "admin123")
        assertTrue("Admin credentials should be valid", isValidAdmin)

        val isInvalid = dbHelper.checkUser("admin@gmail.com", "wrongpassword")
        assertFalse("Incorrect password should return false", isInvalid)
    }

    @Test
    fun testGetUser() {
        val user = dbHelper.getUser("john.smith@gmail.com")
        assertNotNull("User should exist", user)
        assertEquals("Dr. John Smith", user?.fullName)
        assertEquals("Tutor", user?.role)
        assertEquals("Approved", user?.status)
    }

    @Test
    fun testAddAndCheckNewUser() {
        val testEmail = "test.student.unique@gmail.com"
        assertFalse("Email should not exist prior to creation", dbHelper.isEmailExists(testEmail))

        val result = dbHelper.addUser(
            fullName = "Unique Student",
            email = testEmail,
            password = "securePassword123",
            role = "Student",
            bio = "",
            status = "Approved",
            location = "Cape Town"
        )

        assertTrue("addUser should return positive row ID", result > 0)
        assertTrue("isEmailExists should return true", dbHelper.isEmailExists(testEmail))

        val createdUser = dbHelper.getUser(testEmail)
        assertNotNull(createdUser)
        assertEquals("Unique Student", createdUser?.fullName)
    }

    @Test
    fun testPendingUserApproval() {
        val pendingUsers = dbHelper.getPendingUsers()
        assertTrue("Pending users should exist", pendingUsers.isNotEmpty())

        val userToApprove = pendingUsers.first()
        val rowsUpdated = dbHelper.approveUser(userToApprove.email)

        assertTrue("approveUser should update at least 1 row", rowsUpdated > 0)

        val approvedUser = dbHelper.getUser(userToApprove.email)
        assertEquals("Approved", approvedUser?.status)
    }

    @Test
    fun testAddBookingAndRetrieve() {
        val studentName = "Alex Johnson"
        val tutorName = "Dr. John Smith"
        val subject = "Mathematics"
        val date = "20/12/2026"

        val bookingId = dbHelper.addBooking(date, subject, tutorName, studentName)
        assertTrue("Booking insertion should return positive ID", bookingId > 0)

        val studentBookings = dbHelper.getBookingsForStudent(studentName)
        assertTrue("Student should have bookings", studentBookings.isNotEmpty())
        assertTrue("Newly added booking date should match", studentBookings.any { it.date == date && it.subject == subject })

        val tutorBookings = dbHelper.getBookingsForTutor(tutorName)
        assertTrue("Tutor should have bookings", tutorBookings.isNotEmpty())
    }

    @Test
    fun testUpdateGradeAndAttendance() {
        val sessions = dbHelper.getAllSessions()
        assertTrue(sessions.isNotEmpty())

        val targetSession = sessions.first()
        val updatedGrade = "A+"
        val updatedAttendance = "PRESENT"

        val gradeRows = dbHelper.updateGrade(targetSession.id, updatedGrade)
        assertTrue(gradeRows > 0)

        val attendanceRows = dbHelper.updateAttendance(targetSession.id, updatedAttendance)
        assertTrue(attendanceRows > 0)
    }

    @Test
    fun testSearchTutors() {
        val mathTutors = dbHelper.searchTutors("Mathematics", "Select Location")
        assertTrue("Search for Mathematics should return tutors", mathTutors.isNotEmpty())

        val capeTownTutors = dbHelper.searchTutors("Select Subject", "Cape Town")
        assertTrue("Search for Cape Town should return tutors", capeTownTutors.isNotEmpty())
    }
}
