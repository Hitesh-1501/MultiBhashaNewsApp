package com.example.multibhashanews.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.multibhashanews.R
import com.example.multibhashanews.databinding.ActivitySignInBinding
import com.example.multibhashanews.model.UserInfo
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseClass() {
    private lateinit var binding:ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                loginUser(email,password)
            }else{
                customToast(this@SignInActivity,"Empty fields are not allowed!!")
            }
        }

        binding.textView.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))

        }
    }

    private fun loginUser(email:String,password:String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if(task.isSuccessful){
                customToast(this@SignInActivity,"Login Successfully")
                Handler(Looper.getMainLooper()).postDelayed({
                    val userInfo = UserInfo(email, password)
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java).apply {
                        putExtra("userInfo", userInfo)
                    })
                    finish()
                },1000)

            }else{
                customToast(this@SignInActivity,"${task.exception}")
            }
        }
    }
}