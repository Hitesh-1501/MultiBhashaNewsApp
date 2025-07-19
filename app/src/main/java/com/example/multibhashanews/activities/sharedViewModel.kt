package com.example.multibhashanews.activities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel(): ViewModel() {

    val userEmail = MutableLiveData<String>()
}