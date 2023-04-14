package com.example.lfgg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class MembersPage : AppCompatActivity() {

    private lateinit var memberList: ArrayList<String>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MemberAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members_page)

        val chatId = intent.getStringExtra("chatId")

        memberList = ArrayList()
        mDbRef = FirebaseDatabase.getInstance().reference

        viewManager = LinearLayoutManager(this@MembersPage)
        viewAdapter = MemberAdapter(memberList)

        recyclerView = findViewById<RecyclerView>(R.id.membersRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        mDbRef.child("chats").child(chatId!!).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    memberList.clear()

                    for (postSnapshot in snapshot.children) {
                        val userID = postSnapshot.getValue<String>()!!
                        mDbRef.child("user").child(userID!!).child("name").get()
                            .addOnSuccessListener {
                                println(it.getValue<String>()!!)
                                memberList.add(it.getValue<String>()!!)
                                viewAdapter.notifyDataSetChanged()
                            }.addOnFailureListener {
                                println("Failed to read value.")
                            }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    println("Failed to read value.")
                }
            })
    }
}
