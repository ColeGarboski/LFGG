package com.example.lfgg

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {

    private lateinit var edtUser: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        edtUser = findViewById(R.id.edt_username)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignup)

        btnSignUp.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(email, password)
        }
    }

    private fun signUp(email: String, password: String){
        // Creating user logic
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Jump to Login screen
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "An error has occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }
}