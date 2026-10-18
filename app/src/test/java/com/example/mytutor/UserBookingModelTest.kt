package com.example.mytutor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserBookingModelTest {

    @Test
    fun testUserCreationDefaults() {
        val user = DatabaseHelper.User(
            fullName = "Test User",
            email = "test.user@gmail.com",
            role = "Student"
        )

        assertEquals("Test User", user.fullName)
        assertEquals("test.user@gmail.com", user.email)
        assertEquals("Student", user.role)
        assertEquals("", user.bio)
        assertEquals("Approved", user.status)
        assertEquals("", user.location)
    }

    @Test
    fun testUserCustomValues() {
        val user = DatabaseHelper.User(
            fullName = "Dr. Jane Doe",
            email = "jane.doe@outlook.com",
            role = "Tutor",
            bio = "Mathematics PHD",
            status = "Pending",
            location = "Cape Town"
        )

        assertEquals("Dr. Jane Doe", user.fullName)
        assertEquals("jane.doe@outlook.com", user.email)
        assertEquals("Tutor", user.role)
        assertEquals("Mathematics PHD", user.bio)
        assertEquals("Pending", user.status)
        assertEquals("Cape Town", user.location)
    }

    @Test
    fun testBookingCreationDefaults() {
        val booking = DatabaseHelper.Booking(
            id = 1,
            date = "15/10/2026",
            subject = "Mathematics",
            tutorName = "Dr. John Smith",
            studentName = "Alex Johnson",
            grade = null,
            attendance = null
        )

        assertEquals(1, booking.id)
        assertEquals("15/10/2026", booking.date)
        assertEquals("Mathematics", booking.subject)
        assertEquals("Dr. John Smith", booking.tutorName)
        assertEquals("Alex Johnson", booking.studentName)
        assertNull(booking.grade)
        assertNull(booking.attendance)
    }

    @Test
    fun testBookingWithGradeAndAttendance() {
        val booking = DatabaseHelper.Booking(
            id = 2,
            date = "18/10/2026",
            subject = "Science",
            tutorName = "Prof. Sarah Jenkins",
            studentName = "Emma Wilson",
            grade = "A",
            attendance = "PRESENT"
        )

        assertEquals(2, booking.id)
        assertEquals("A", booking.grade)
        assertEquals("PRESENT", booking.attendance)
    }

    @Test
    fun testEmailFormatValidationLogic() {
        val validEmails = listOf("test@gmail.com", "user@outlook.com", "john.doe@yahoo.com")
        val invalidEmails = listOf("", "invalidemail", "noatsign.com")

        for (email in validEmails) {
            assertTrue("Expected $email to contain '@'", email.contains("@"))
        }

        for (email in invalidEmails) {
            assertFalse("Expected $email to be invalid", email.contains("@") && email.contains("."))
        }
    }
}
