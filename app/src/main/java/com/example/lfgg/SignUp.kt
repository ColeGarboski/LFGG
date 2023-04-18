package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtUser: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var spnPlatform: Spinner
    private lateinit var spnGames: Spinner
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        edtUser = findViewById(R.id.edt_username)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignup)
        spnPlatform = findViewById(R.id.spn_platform)
        spnGames = findViewById(R.id.spn_games)

        btnSignUp.setOnClickListener {
            val user = edtUser.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val platform = spnPlatform.selectedItem.toString()
            val game = spnGames.selectedItem.toString()

            signUp(user, email, password, platform, game)
        }
    }

    private fun signUp(user: String, email: String, password: String, platform: String, game: String){

        // Creating user logic
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Jump to Login screen
                    addUserToDatabase(user, email, platform, game, mAuth.currentUser?.uid!!)
                    val intent = Intent(this@SignUp, Login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "An error has occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(user: String, email: String, platform: String, game: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("user").child(uid).setValue(User(user, email, platform, game, uid))
    }
}
