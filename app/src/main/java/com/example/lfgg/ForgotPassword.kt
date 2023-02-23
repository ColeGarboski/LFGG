package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.EditText

class ForgotPassword : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editEmail = findViewById(R.id.edit_email)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        btnChangePassword.setOnClickListener {
            val email = editEmail.text.toString()

            //changePassword(email)

            Toast.makeText(this@ForgotPassword, "Email Sent!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@ForgotPassword, Login::class.java)
            startActivity(intent)

        }
    }
}