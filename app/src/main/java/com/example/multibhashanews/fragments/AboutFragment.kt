package com.example.multibhashanews.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.multibhashanews.R
import com.example.multibhashanews.activities.AppInfoActivity
import com.example.multibhashanews.activities.BaseClass
import com.example.multibhashanews.activities.SharedViewModel
import com.example.multibhashanews.activities.SignInActivity
import com.example.multibhashanews.databinding.FragmentAboutBinding
import com.google.firebase.BuildConfig
import com.google.firebase.auth.FirebaseAuth

class AboutFragment : Fragment(R.layout.fragment_about) {
    lateinit var binding: FragmentAboutBinding
    private lateinit var firebaseAuth: FirebaseAuth
    // Get the *same* ViewModel instance that the Activity is using
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutBinding.bind(view)

        firebaseAuth = FirebaseAuth.getInstance()

        // Observe the LiveData. The code inside the brackets will run automatically
        // as soon as the email is available, or immediately if it's already there.
        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvUserEmail.text = email ?: "No Email Found"
        }

        binding.cvLogout.setOnClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle("Logout")
                setMessage("Do you want to logout this acc?")
                setPositiveButton("Yes"){_,_->
                    firebaseAuth.signOut()
                    val intent = Intent(requireContext(),SignInActivity::class.java).apply {
                        // These flags clear the back stack and start a new task
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    requireActivity().finish()
                }
                setNegativeButton("Cancel",null)
            }.create().show()
        }
       binding.cvPrivacyPolicy.setOnClickListener{
           (activity as BaseClass).customToast(requireContext(),"This section is derived from future updates")
       }
        binding.cvRateApp.setOnClickListener{
            (activity as BaseClass).customToast(requireContext(),"This section is derived from future updates")
        }
        binding.cvAppInfo.setOnClickListener {
            startActivity(Intent(requireContext(),AppInfoActivity::class.java))
        }
    }
}