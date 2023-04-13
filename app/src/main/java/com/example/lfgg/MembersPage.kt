package com.example.lfgg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class MembersPage : AppCompatActivity() {

    private lateinit var memberList: ArrayList<String>
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members_page)

        val chatId = intent.getStringExtra("chatId")

        memberList = ArrayList()
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("chats").child(chatId!!).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    memberList.clear()

                    for (postSnapshot in snapshot.children) {
                        memberList.add(postSnapshot.getValue<String>()!!)
                    }

                    //for (member in memberList) { println(member) }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    println("Failed to read value.")
                }
            })
    }

}