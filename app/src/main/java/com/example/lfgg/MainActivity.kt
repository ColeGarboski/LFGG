package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var chatList: ArrayList<ChatObject>
    private lateinit var btnNewChat: Button
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var platformSelection: String //platform requested by the user (i wanted more than one at once but that is nasty..maybe later)
    private lateinit var gameSelection: String //string of game selected (i wanted more than one at once but that is nasty..maybe later)
    private var playerCountSelection: Int = 0//this is the number of players missing from a lobby (whenever the user enters it, change this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        platformSelection = "Xbox"  //some tests
        gameSelection = "xbox"
        playerCountSelection = 4


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        chatList = ArrayList()
        adapter = UserAdapter(this, chatList)
        btnNewChat = findViewById(R.id.btnNewChat)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(postSnapshot in snapshot.children){
                    val currentChat = postSnapshot.getValue(ChatObject::class.java)
                    currentChat!!.chatId = postSnapshot.key

                    //condition to filter by user choice of Platform(xbox,pc,playstation), Game(COD,Destiny,BungoBros), missing players(chat maximum minus chat current)
                    val playersMissing = currentChat.maxPlayers?.minus(currentChat.currentPlayers) //number of players missing
                    if(    (playersMissing != null && playersMissing <= playerCountSelection)    && (currentChat.gameName == gameSelection) && (currentChat.platform == platformSelection)   && (playersMissing != 0)  )
                    {

                        chatList.add(currentChat!!) //add currentChat to array of chats to be displayed only if it matches the filters from the user

                    }
                }
                //Chatlist sort (for zach)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        btnNewChat.setOnClickListener {
            val intent = Intent(this, NewChat::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout) {
            //Logic for logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}