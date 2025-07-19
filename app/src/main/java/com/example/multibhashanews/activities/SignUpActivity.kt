package com.example.multibhashanews.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.multibhashanews.R
import com.example.multibhashanews.databinding.ActivitySignUpBinding
import com.example.multibhashanews.model.UserInfo
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : BaseClass() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()){
                if(password == confirmPass){
                    signUpUser(email,password)

                }else{
                    customToast(this@SignUpActivity,"password is not matching")
                }
            }else{
                customToast(this@SignUpActivity,"Empty fields are not allowed!!")
            }
        }
        binding.goToSignIn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }
    private fun signUpUser(email:String, password:String){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){
                customToast(this,"Successfully Registered")
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                },1000)

            }else{
                customToast(this@SignUpActivity,"${task.exception}")
            }
        }
    }

}