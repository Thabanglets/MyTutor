package com.example.mytutor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyTutor.db"
        private const val DATABASE_VERSION = 12 // Incremented to 12 to update email domains to gmail/outlook/yahoo

        // User table
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"
        const val COLUMN_BIO = "bio"
        const val COLUMN_STATUS = "status"
        const val COLUMN_LOCATION = "location"

        // Booking table
        const val TABLE_BOOKINGS = "bookings"
        const val COLUMN_BOOKING_ID = "booking_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_SUBJECT = "subject"
        const val COLUMN_TUTOR_NAME = "tutor_name"
        const val COLUMN_STUDENT_NAME = "student_name"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_ATTENDANCE = "attendance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_BIO + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_LOCATION + " TEXT" + ")")
        
        val createBookingTable = ("CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TUTOR_NAME + " TEXT,"
                + COLUMN_STUDENT_NAME + " TEXT,"
                + COLUMN_GRADE + " TEXT,"
                + COLUMN_ATTENDANCE + " TEXT" + ")")

        db?.execSQL(createUserTable)
        db?.execSQL(createBookingTable)

        seedInitialData(db)
    }

    private fun seedInitialData(db: SQLiteDatabase?) {
        preloadAdmin(db)
        preloadTutors(db)
        preloadStudents(db)
        preloadPendingUsers(db)
        preloadBookings(db)
    }

    private fun preloadAdmin(db: SQLiteDatabase?) {
        insertUserIfNotExists(db, "Admin User", "admin@gmail.com", "admin123", "Admin", "System Administrator", "Approved", "Cape Town")
    }

    private fun preloadTutors(db: SQLiteDatabase?) {
        val tutors = arrayOf(
            arrayOf("Dr. John Smith", "johnsmith@gmail.com", "password123", "Tutor", "Expert in Advanced Mathematics with 10 years of experience.", "Cape Town"),
            arrayOf("Prof. Sarah Jenkins", "sarahjenkins@outlook.com", "password123", "Tutor", "Specializes in Organic Chemistry, Biology and Science.", "Johannesburg"),
            arrayOf("Michael Chen", "michaelchen@yahoo.com", "password123", "Tutor", "Native English speaker focused on Literature, Grammar and Writing.", "Durban"),
            arrayOf("Dr. Emily Davis", "emilydavis@gmail.com", "password123", "Tutor", "Physics PhD with a passion for teaching high school Science students.", "Online"),
            arrayOf("David Ndlovu", "davidndlovu@outlook.com", "password123", "Tutor", "Software Engineer teaching Programming, Data Structures and Computer Science.", "Cape Town"),
            arrayOf("Amanda Naidoo", "amandanaidoo@yahoo.com", "password123", "Tutor", "Experienced History educator specializing in World and African History.", "Johannesburg"),
            arrayOf("Robert Taylor", "roberttaylor@gmail.com", "password123", "Tutor", "Geography and Earth Sciences specialist with practical field experience.", "Durban"),
            arrayOf("Lisa Mokoena", "lisamokoena@outlook.com", "password123", "Tutor", "University Mathematics lecturer focusing on Algebra and Calculus.", "Online")
        )

        for (tutor in tutors) {
            insertUserIfNotExists(db, tutor[0], tutor[1], tutor[2], tutor[3], tutor[4], "Approved", tutor[5])
        }
    }

    private fun preloadStudents(db: SQLiteDatabase?) {
        val students = arrayOf(
            arrayOf("Alex Johnson", "alexjohnson@gmail.com", "password123", "Cape Town"),
            arrayOf("Emma Wilson", "emmawilson@outlook.com", "password123", "Johannesburg"),
            arrayOf("Sipho Zulu", "siphozulu@yahoo.com", "password123", "Durban"),
            arrayOf("Liam Botha", "liambotha@gmail.com", "password123", "Cape Town"),
            arrayOf("Chloe Pillay", "chloepillay@outlook.com", "password123", "Durban"),
            arrayOf("Thabo Mbeki", "thabombeki@yahoo.com", "password123", "Johannesburg")
        )
        for (student in students) {
            insertUserIfNotExists(db, student[0], student[1], student[2], "Student", "", "Approved", student[3])
        }
    }

    private fun preloadPendingUsers(db: SQLiteDatabase?) {
        val pendingUsers = arrayOf(
            arrayOf("James Peterson", "jamespeterson@gmail.com", "password123", "Tutor", "Mathematics teacher awaiting approval.", "Cape Town"),
            arrayOf("Maria Garcia", "mariagarcia@outlook.com", "password123", "Tutor", "Science tutor specializing in Biology.", "Online"),
            arrayOf("Jessica Brown", "jessicabrown@yahoo.com", "password123", "Student", "", "Johannesburg")
        )
        for (user in pendingUsers) {
            insertUserIfNotExists(db, user[0], user[1], user[2], user[3], user[4], "Pending", user[5])
        }
    }

    private fun insertUserIfNotExists(
        db: SQLiteDatabase?,
        fullName: String,
        email: String,
        password: String,
        role: String,
        bio: String,
        status: String,
        location: String
    ) {
        val cursor = db?.rawQuery("SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor?.use { it.count > 0 } ?: false
        if (!exists) {
            val values = ContentValues().apply {
                put(COLUMN_FULL_NAME, fullName)
                put(COLUMN_EMAIL, email)
                put(COLUMN_PASSWORD, password)
                put(COLUMN_ROLE, role)
                put(COLUMN_BIO, bio)
                put(COLUMN_STATUS, status)
                put(COLUMN_LOCATION, location)
            }
            db?.insert(TABLE_USERS, null, values)
        }
    }

    private fun preloadBookings(db: SQLiteDatabase?) {
        val cursor = db?.rawQuery("SELECT COUNT(*) FROM $TABLE_BOOKINGS", null)
        val count = cursor?.use { if (it.moveToFirst()) it.getInt(0) else 0 } ?: 0
        if (count >= 10) return

        val bookingsData = arrayOf(
            arrayOf("12/10/2026", "Mathematics", "Dr. John Smith", "Alex Johnson", "A", "PRESENT"),
            arrayOf("14/10/2026", "Science", "Prof. Sarah Jenkins", "Alex Johnson", "88%", "PRESENT"),
            arrayOf("15/10/2026", "English", "Michael Chen", "Alex Johnson", "B+", "PRESENT"),
            arrayOf("18/10/2026", "Programming", "David Ndlovu", "Alex Johnson", "95%", "PRESENT"),
            arrayOf("20/10/2026", "Mathematics", "Lisa Mokoena", "Emma Wilson", "A-", "PRESENT"),
            arrayOf("22/10/2026", "Science", "Dr. Emily Davis", "Emma Wilson", "91%", "PRESENT"),
            arrayOf("24/10/2026", "History", "Amanda Naidoo", "Emma Wilson", "B", "PRESENT"),
            arrayOf("25/10/2026", "Geography", "Robert Taylor", "Sipho Zulu", "82%", "PRESENT"),
            arrayOf("26/10/2026", "Mathematics", "Dr. John Smith", "Sipho Zulu", "A", "PRESENT"),
            arrayOf("28/10/2026", "Programming", "David Ndlovu", "Liam Botha", "89%", "PRESENT"),
            arrayOf("30/10/2026", "English", "Michael Chen", "Chloe Pillay", "A+", "PRESENT"),
            arrayOf("02/11/2026", "Science", "Prof. Sarah Jenkins", "Thabo Mbeki", "78%", "PRESENT"),
            arrayOf("05/11/2026", "Geography", "Robert Taylor", "Alex Johnson", "Pending", "ABSENT"),
            arrayOf("08/11/2026", "Mathematics", "Dr. John Smith", "Emma Wilson", null, null),
            arrayOf("10/11/2026", "History", "Amanda Naidoo", "Sipho Zulu", null, null)
        )

        for (booking in bookingsData) {
            val values = ContentValues().apply {
                put(COLUMN_DATE, booking[0])
                put(COLUMN_SUBJECT, booking[1])
                put(COLUMN_TUTOR_NAME, booking[2])
                put(COLUMN_STUDENT_NAME, booking[3])
                if (booking[4] != null) put(COLUMN_GRADE, booking[4])
                if (booking[5] != null) put(COLUMN_ATTENDANCE, booking[5])
            }
            db?.insert(TABLE_BOOKINGS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            try {
                db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_STATUS TEXT DEFAULT 'Approved'")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding status column: ${e.message}")
            }
        }
        if (oldVersion < 7) {
            try {
                addColumnIfNotExists(db, TABLE_BOOKINGS, COLUMN_GRADE, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding grade column: ${e.message}")
            }
        }
        if (oldVersion < 8) {
            try {
                addColumnIfNotExists(db, TABLE_BOOKINGS, COLUMN_ATTENDANCE, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding attendance column: ${e.message}")
            }
        }
        if (oldVersion < 10) {
            try {
                addColumnIfNotExists(db, TABLE_USERS, COLUMN_LOCATION, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding location column: ${e.message}")
            }
        }
        if (oldVersion < 12) {
            try {
                seedInitialData(db)
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error seeding data during upgrade: ${e.message}")
            }
        }
    }

    private fun addColumnIfNotExists(db: SQLiteDatabase?, tableName: String, columnName: String, columnType: String) {
        val cursor = db?.rawQuery("PRAGMA table_info($tableName)", null)
        var exists = false
        if (cursor != null) {
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex != -1 && cursor.getString(nameIndex) == columnName) {
                    exists = true
                    break
                }
            }
            cursor.close()
        }
        if (!exists) {
            db?.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnType")
        }
    }

    fun addUser(fullName: String, email: String, password: String, role: String, bio: String = "", status: String = "Approved", location: String = ""): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_ROLE, role)
        values.put(COLUMN_BIO, bio)
        values.put(COLUMN_STATUS, status)
        values.put(COLUMN_LOCATION, location)
        
        return try {
            val result = db.insert(TABLE_USERS, null, values)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Failed to insert user: $email")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting user: ${e.message}")
            -1L
        }
    }

    fun approveUser(email: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, "Approved")
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    fun getPendingUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_STATUS = ?", arrayOf("Pending"), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                users.add(User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    fun getAllSessions(): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, null, null, null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, password), null, null, null)
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getUser(email: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) ?: "Approved",
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
            )
        }
        cursor.close()
        return user
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    data class User(val fullName: String, val email: String, val role: String, val bio: String = "", val status: String = "Approved", val location: String = "")
    data class Booking(val id: Int, val date: String, val subject: String, val tutorName: String, val studentName: String, val grade: String?, val attendance: String?)

    fun addBooking(date: String, subject: String, tutorName: String, studentName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_SUBJECT, subject)
        values.put(COLUMN_TUTOR_NAME, tutorName)
        values.put(COLUMN_STUDENT_NAME, studentName)
        return db.insert(TABLE_BOOKINGS, null, values)
    }

    fun getBookingsForStudent(studentName: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, "$COLUMN_STUDENT_NAME = ?", arrayOf(studentName), null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME)),
                    studentName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun getBookingsForTutor(tutorName: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, "$COLUMN_TUTOR_NAME = ?", arrayOf(tutorName), null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    tutorName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun updateGrade(bookingId: Int, grade: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_GRADE, grade)
        return db.update(TABLE_BOOKINGS, values, "$COLUMN_BOOKING_ID = ?", arrayOf(bookingId.toString()))
    }

    fun updateAttendance(bookingId: Int, attendance: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ATTENDANCE, attendance)
        return db.update(TABLE_BOOKINGS, values, "$COLUMN_BOOKING_ID = ?", arrayOf(bookingId.toString()))
    }

    fun updateUser(email: String, fullName: String, bio: String, password: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_BIO, bio)
        values.put(COLUMN_PASSWORD, password)
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    fun getAllTutorsWithBio(): List<User> {
        val tutorList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_FULL_NAME, $COLUMN_EMAIL, $COLUMN_ROLE, $COLUMN_BIO, $COLUMN_STATUS, $COLUMN_LOCATION FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'Tutor' AND $COLUMN_STATUS = 'Approved'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                tutorList.add(User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3) ?: "",
                    cursor.getString(4) ?: "Approved",
                    cursor.getString(5) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tutorList
    }

    fun searchTutors(subject: String?, location: String?): List<User> {
        val tutorList = mutableListOf<User>()
        val db = this.readableDatabase
        
        var selection = "$COLUMN_ROLE = 'Tutor' AND $COLUMN_STATUS = 'Approved'"
        val selectionArgs = mutableListOf<String>()

        if (!subject.isNullOrEmpty() && subject != "Select Subject") {
            selection += " AND $COLUMN_BIO LIKE ?"
            selectionArgs.add("%$subject%")
        }

        if (!location.isNullOrEmpty() && location != "Select Location") {
            selection += " AND $COLUMN_LOCATION = ?"
            selectionArgs.add(location)
        }

        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs.toTypedArray(), null, null, null)
        
        if (cursor.moveToFirst()) {
            do {
                tutorList.add(User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) ?: "Approved",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tutorList
    }

    fun getAllStudents(): List<String> {
        val studentList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_FULL_NAME FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'Student' AND $COLUMN_STATUS = 'Approved'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                studentList.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return studentList
    }

    fun getTutorBio(name: String): String {
        var bio = ""
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_BIO FROM $TABLE_USERS WHERE $COLUMN_FULL_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(name))
        if (cursor.moveToFirst()) {
            bio = cursor.getString(0) ?: ""
        }
        cursor.close()
        return bio
    }

    fun getUserPassword(email: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_PASSWORD), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        var password: String? = null
        if (cursor.moveToFirst()) {
            password = cursor.getString(0)
        }
        cursor.close()
        return password
    }

    /**
     * Explicitly opens and returns a writable database connection.
     */
    fun openDatabase(): SQLiteDatabase {
        return this.writableDatabase
    }

    /**
     * Explicitly closes the database connection.
     */
    fun closeDatabase() {
        this.close()
    }
}
