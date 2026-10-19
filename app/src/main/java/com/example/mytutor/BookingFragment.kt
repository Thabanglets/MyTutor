package com.example.mytutor

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mytutor.databinding.FragmentBookingBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.Calendar

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedDate: String = ""
    private var selectedTime: String = "10:00"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        // Set default date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        selectedDate = "$day/${month + 1}/$year"
        binding.dateAutoComplete.setText(selectedDate)

        binding.dateAutoComplete.setOnClickListener {
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.dateAutoComplete.setText(selectedDate)
            }, year, month, day).show()
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

            timePicker.show(childFragmentManager, "LESSON_TIME_PICKER")
        }

        // Setup Subject Dropdown
        val subjects = resources.getStringArray(R.array.subjects_array)
        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects)
        binding.subjectAutoComplete.setAdapter(subjectAdapter)

        // Setup Tutor Dropdown with Bio
        val tutors = databaseHelper.getAllTutorsWithBio()
        val adapter = TutorAdapter(requireContext(), tutors)
        binding.tutorAutoComplete.setAdapter(adapter)

        binding.tutorAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedTutor = parent.getItemAtPosition(position) as DatabaseHelper.User
            binding.tutorAutoComplete.setText(selectedTutor.fullName, false)
            binding.tutorBioText.text = selectedTutor.bio
            binding.tutorBioText.visibility = View.VISIBLE
        }

        binding.btnBookNow.setOnClickListener {
            val subject = binding.subjectAutoComplete.text.toString().trim()
            val tutorName = binding.tutorAutoComplete.text.toString().trim()

            if (subject.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tutorName.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a tutor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve student name from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val studentName = sharedPreferences.getString("userName", "Anonymous Student") ?: "Anonymous Student"

            val result = databaseHelper.addBooking(selectedDate, subject, tutorName, studentName)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Booking Successful for $tutorName!", Toast.LENGTH_SHORT).show()

                fun navigateHome() {
                    if (!isAdded) return
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, HomeFragment())
                        .commit()

                    (activity as? HomeActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.home
                }

                // Check calendar sync via Nylas
                if (binding.switchGoogleCalendar.isChecked) {
                    lifecycleScope.launch {
                        val nylasResult = NylasCalendarHelper.insertEventToNylas(
                            title = "$subject Tutoring Lesson",
                            description = "MyTutor lesson with $tutorName",
                            dateStr = selectedDate,
                            timeStr = selectedTime
                        )
                        if (isAdded) {
                            nylasResult.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Synced to Nylas Calendar!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = {
                                    val intent = NylasCalendarHelper.createCalendarIntent(subject, tutorName, selectedDate, selectedTime)
                                    startActivity(intent)
                                }
                            )
                        }
                        navigateHome()
                    }
                } else {
                    navigateHome()
                }
            } else {
                Toast.makeText(requireContext(), "Booking Failed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
