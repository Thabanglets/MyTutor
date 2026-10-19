package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mytutor.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userEmail: String
    private var userRole: String = "Student"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("userEmail", "") ?: ""
        userRole = sharedPreferences.getString("userRole", "Student") ?: "Student"

        loadUserData()
        setupSettingsPreferences()

        binding.btnSaveProfile.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnContactSupport.setOnClickListener {
            showSupportDialog()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        return binding.root
    }

    private fun loadUserData() {
        if (userEmail.isEmpty()) return

        val user = databaseHelper.getUser(userEmail)
        user?.let {
            binding.tvProfileName.text = it.fullName
            binding.tvProfileEmail.text = it.email
            binding.tvProfileRole.text = it.role.uppercase()

            binding.etFullName.setText(it.fullName.ifEmpty { "" })
            binding.etEmail.setText(it.email.ifEmpty { "" })
            
            if (it.location.isNotBlank()) {
                binding.etLocation.setText(it.location)
            } else {
                binding.etLocation.setText("")
            }

            if (it.bio.isNotBlank()) {
                binding.etBio.setText(it.bio)
            } else {
                binding.etBio.setText("")
            }

            if (it.role == "Tutor") {
                binding.bioLayout.visibility = View.VISIBLE
            } else {
                binding.bioLayout.visibility = View.GONE
            }
        }
    }

    private fun setupSettingsPreferences() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Dark Mode Preference
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply()
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            Toast.makeText(
                requireContext(),
                if (isChecked) "Dark Mode Enabled" else "Light Mode Enabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Notification Preference
        val notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true)
        binding.switchNotifications.isChecked = notificationsEnabled

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
            Toast.makeText(
                requireContext(),
                if (isChecked) "Session Notifications Enabled" else "Session Notifications Disabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showSupportDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Help & Support")
            .setMessage(
                "Need assistance or have feedback?\n\n" +
                        "• Support Email: admin@gmail.com\n" +
                        "• Operating Hours: Mon - Fri (8:00 - 17:00)\n" +
                        "• Version: MyTutor v1.0.0\n\n" +
                        "For urgent session changes, please contact your tutor or student directly."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun saveProfileChanges() {
        val fullName = binding.etFullName.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Full name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val finalPassword = if (password.isEmpty()) {
            databaseHelper.getUserPassword(userEmail) ?: ""
        } else {
            password
        }

        if (finalPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Error retrieving user security data", Toast.LENGTH_SHORT).show()
            return
        }

        val result = databaseHelper.updateUser(userEmail, fullName, bio, finalPassword)
        if (result > 0) {
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("userName", fullName)
                .putString("userLocation", location)
                .apply()

            binding.tvProfileName.text = fullName

            Toast.makeText(requireContext(), "Settings & Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
