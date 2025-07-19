package com.example.multibhashanews.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.multibhashanews.databinding.ActivityBaseBinding
import com.example.multibhashanews.databinding.CustomToastBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlin.io.path.fileVisitor

open class BaseClass: AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun customToast(context: Context, message: String) {
        val binding = CustomToastBinding.inflate(LayoutInflater.from(context))
        binding.toastMessage.text = message
        val toast = Toast(context)
        toast.view = binding.root
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }

    @SuppressLint("RestrictedApi")
    fun showCustomSnackBar(parentView: View, message: String) {
        val snackbar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        val binding = CustomToastBinding.inflate(LayoutInflater.from(parentView.context))
        binding.toastMessage.text = message


        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(binding.root,0)

        snackbar.show()
    }
}
