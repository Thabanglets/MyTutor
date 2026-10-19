package com.example.mytutor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.mytutor.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentHomeBinding? = null
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val studentName = sharedPreferences.getString("userName", "Student") ?: "Student"
        
        binding.welcomeText.text = "Welcome back, \n$studentName! 👋"

        // Load sessions
        loadSessions(studentName)

        binding.findTutor.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, FindTutorFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.profileIcon.setOnClickListener {
            showLogoutMenu(it)
        }
    }

    private fun showLogoutMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add("Logout")
        popupMenu.menu.add("Settings")
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == "Logout") {
                logout()
                true
            }else if(item.title =="Settings"){
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, ProfileFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
                else {
                false
            }
        }
        popupMenu.show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadSessions(studentName: String) {
        val bookings = databaseHelper.getBookingsForStudent(studentName)
        val adapter = SessionAdapter(requireContext(), bookings, isTutor = false)
        binding.sessionListView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
