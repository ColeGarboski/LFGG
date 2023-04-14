package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var memberButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var memberList: ArrayList<String>
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatName = intent.getStringExtra("chatName")
        val chatId = intent.getStringExtra("chatId")
        mDbRef = FirebaseDatabase.getInstance().reference

        supportActionBar?.title = chatName

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        memberButton = findViewById(R.id.btnMemberCount)
        messageList = ArrayList()
        memberList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //Logic for adding data to recyclerView
        mDbRef.child("chats").child(chatId!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                        if (memberList.contains(message.senderId)) {
                            continue
                        } else {
                            memberList.add(message.senderId!!)
                        }
                    }

                    memberButton.text = "Members: ${memberList.size}"
                    mDbRef.child("chats").child(chatId).child("members").setValue(memberList)
                    mDbRef.child("chats").child(chatId).child("currentPlayers").setValue(memberList.size)

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        // Adding message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val senderUid = FirebaseAuth.getInstance().uid
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(chatId!!).child("messages").push()
                .setValue(messageObject)
            messageBox.setText("")
        }

        memberButton.setOnClickListener {
            val intent = Intent(this, MembersPage::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }
    }
}