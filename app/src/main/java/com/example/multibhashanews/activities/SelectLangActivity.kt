package com.example.multibhashanews.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.multibhashanews.R
import com.example.multibhashanews.databinding.ActivitySelectLangBinding

class SelectLangActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectLangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLangBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}