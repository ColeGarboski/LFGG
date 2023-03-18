package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewChat : AppCompatActivity() {

    private lateinit var edtChatTitle: EditText
    private lateinit var edtGameTitle: EditText
    private lateinit var edtMaxPlayers: EditText
    private lateinit var btnCreateChat: Button
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        edtChatTitle = findViewById(R.id.edttxtChatTitle)
        edtGameTitle = findViewById(R.id.edttxtGameTitle)
        edtMaxPlayers = findViewById(R.id.edtnumMaxPlayers)
        btnCreateChat = findViewById(R.id.btnAddChat)
        mDbRef = FirebaseDatabase.getInstance().reference


        btnCreateChat.setOnClickListener {
            var newKey = mDbRef.child("chats").push().key
            mDbRef.child("chats").child(newKey!!).child("chatName").setValue(edtChatTitle.text.toString())
            mDbRef.child("chats").child(newKey!!).child("gameName").setValue(edtGameTitle.text.toString())
            mDbRef.child("chats").child(newKey!!).child("maxPlayers").setValue(edtMaxPlayers.text.toString())
            mDbRef.child("chats").child(newKey!!).child("currentPlayers").setValue("1")

            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }
}