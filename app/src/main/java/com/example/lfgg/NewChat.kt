package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
class NewChat : AppCompatActivity() {

    private lateinit var edtChatTitle: EditText
    private lateinit var spinGameTitle: Spinner
    private lateinit var spinPlatform: Spinner
    private lateinit var edtMaxPlayers: EditText
    private lateinit var btnCreateChat: Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        edtChatTitle = findViewById(R.id.edttxtChatTitle)
        edtMaxPlayers = findViewById(R.id.edtnumMaxPlayers)
        btnCreateChat = findViewById(R.id.btnAddChat)
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        spinGameTitle = findViewById(R.id.spinGameTitle)
        spinPlatform = findViewById(R.id.spinPlatform)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.gameTitlesArray, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinGameTitle.adapter = adapter
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.platformsArray, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinPlatform.adapter = adapter
        }



        btnCreateChat.setOnClickListener {
            var newKey = mDbRef.child("chats").push().key
            mDbRef.child("chats").child(newKey!!).child("chatName").setValue(edtChatTitle.text.toString())
            mDbRef.child("chats").child(newKey!!).child("gameName").setValue(spinGameTitle.selectedItem.toString())
            mDbRef.child("chats").child(newKey!!).child("maxPlayers").setValue(edtMaxPlayers.text.toString().toInt()) //was .text.tostring...
            mDbRef.child("chats").child(newKey!!).child("currentPlayers").setValue(1)
            mDbRef.child("chats").child(newKey!!).child("platform").setValue(spinPlatform.selectedItem.toString())
            val memberList = ArrayList<String>()
            memberList.add(mAuth.currentUser!!.uid)
            mDbRef.child("chats").child(newKey!!).child("members").setValue(memberList)
            val localDateTime = LocalDateTime.now(ZoneOffset.UTC)
            val formattedDateTime = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) //formats LocalDateTime to string. Otherwise, the database dynamically creates like 10 subFolders which is cool but hard to pull
            mDbRef.child("chats").child(newKey!!).child("timeCreated").setValue(formattedDateTime) //does not need ui

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}