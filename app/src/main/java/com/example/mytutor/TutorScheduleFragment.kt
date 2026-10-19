package com.example.mytutor

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mytutor.databinding.FragmentTutorScheduleBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.Calendar

class TutorScheduleFragment : Fragment() {

    private var _binding: FragmentTutorScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedDate: String = ""
    private var selectedTime: String = "10:00"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorScheduleBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        // Set default date
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

        binding.scheduleCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        // Setup Time Picker
        binding.timeAutoComplete.setText("10:00 AM")
        binding.timeAutoComplete.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(10)
                .setMinute(0)
                .setTitleText("Select Lesson Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                selectedTime = String.format("%02d:%02d", hour, minute)

                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour % 12 == 0) 12 else hour % 12
                val displayTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
                binding.timeAutoComplete.setText(displayTime)
            }

            timePicker.show(childFragmentManager, "TUTOR_LESSON_TIME_PICKER")
        }

        // Setup Student Dropdown
        val students = databaseHelper.getAllStudents()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, students)
        binding.studentAutoComplete.setAdapter(adapter)

        // Setup Dynamic Subjects
        setupDynamicSubjects()

        binding.btnScheduleLesson.setOnClickListener {
            val studentName = binding.studentAutoComplete.text.toString().trim()
            val selectedSubjectId = binding.tutorSubjectGroup.checkedRadioButtonId

            if (studentName.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a student", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSubjectId == -1) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val radioButton = binding.root.findViewById<RadioButton>(selectedSubjectId)
            val subject = radioButton.text.toString()

            // Retrieve tutor name from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val tutorName = sharedPreferences.getString("userName", "Anonymous Tutor") ?: "Anonymous Tutor"

            val result = databaseHelper.addBooking(selectedDate, subject, tutorName, studentName)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Lesson scheduled for $studentName!", Toast.LENGTH_SHORT).show()

                fun navigateTutorHome() {
                    if (!isAdded) return
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, TutorHomeFragment())
                        .commit()

                    (activity as? HomeActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.home
                }

                // Sync to Nylas Calendar
                if (binding.switchGoogleCalendar.isChecked) {
                    lifecycleScope.launch {
                        val nylasResult = NylasCalendarHelper.insertEventToNylas(
                            title = "$subject Tutoring Lesson",
                            description = "MyTutor lesson with $studentName",
                            dateStr = selectedDate,
                            timeStr = selectedTime
                        )
                        if (isAdded) {
                            nylasResult.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Synced to Nylas Calendar!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = {
                                    val intent = NylasCalendarHelper.createCalendarIntent(subject, studentName, selectedDate, selectedTime)
                                    startActivity(intent)
                                }
                            )
                        }
                        navigateTutorHome()
                    }
                } else {
                    navigateTutorHome()
                }
            } else {
                Toast.makeText(requireContext(), "Scheduling Failed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun setupDynamicSubjects() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val tutorName = sharedPreferences.getString("userName", "") ?: ""

        val tutorBio = databaseHelper.getTutorBio(tutorName)
        val pastBookings = databaseHelper.getBookingsForTutor(tutorName)
        val allPossibleSubjects = resources.getStringArray(R.array.subjects_array)

        val tutorSubjects = mutableSetOf<String>()

        // 1. Add subjects from past bookings
        pastBookings.forEach { tutorSubjects.add(it.subject) }

        // 2. Add subjects mentioned in bio
        allPossibleSubjects.forEach { subject ->
            if (tutorBio.contains(subject, ignoreCase = true)) {
                tutorSubjects.add(subject)
            }
        }

        if (tutorSubjects.isEmpty()) {
            if (tutorBio.contains("Math", ignoreCase = true)) tutorSubjects.add("Mathematics")
            if (tutorBio.contains("Science", ignoreCase = true)) tutorSubjects.add("Science")
            if (tutorBio.contains("English", ignoreCase = true)) tutorSubjects.add("English")
        }

        binding.tutorSubjectGroup.removeAllViews()
        tutorSubjects.forEach { subject ->
            val rb = RadioButton(requireContext())
            rb.text = subject
            rb.id = View.generateViewId()
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            rb.layoutParams = params
            binding.tutorSubjectGroup.addView(rb)
        }

        if (binding.tutorSubjectGroup.childCount > 0) {
            (binding.tutorSubjectGroup.getChildAt(0) as RadioButton).isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
