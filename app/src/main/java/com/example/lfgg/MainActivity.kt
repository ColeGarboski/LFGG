package com.example.lfgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Duration
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var chatList: ArrayList<ChatObject>
    private lateinit var btnNewChat: Button
    private lateinit var spinGameTitle: Spinner
    private lateinit var spinPlatform: Spinner
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var platformSelection: String //platform requested by the user (i wanted more than one at once but that is nasty..maybe later)
    private lateinit var gameSelection: String //string of game selected (i wanted more than one at once but that is nasty..maybe later)
    private var playerCountSelection: Int =
        0  //this is the number of players missing from a lobby (whenever the user enters it, change this) we will return any lobby with this many openings or more (maybe change later)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinGameTitle = findViewById(R.id.spinGameTitle)
        spinPlatform = findViewById(R.id.spinPlatform)


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        chatList = ArrayList()
        adapter = UserAdapter(this, chatList)
        btnNewChat = findViewById(R.id.btnNewChat)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.gameTitlesArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinGameTitle.adapter = adapter
            //println("Game Title Spinner Set")
        }

        spinGameTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                gameSelection = spinGameTitle.selectedItem.toString()
                //println("Game Title Spinner Selected: $gameSelection")
                fetchAndUpdateChats()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        //Set default to players favorite
        mDbRef.child("user").child(mAuth.currentUser!!.uid).child("game").get()
            .addOnSuccessListener {
                //println("Favorite Game value from db: ${it.value.toString()}")
                for (i in 0 until spinGameTitle.count) {
                    if (spinGameTitle.getItemAtPosition(i).toString() == it.value.toString()) {
                        spinGameTitle.setSelection(i)
                        //println("Game Title Spinner Set to Favorite: $it.value.toString()")
                    }
                }
            }.addOnFailureListener {
                println("piss")
            }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.platformsArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinPlatform.adapter = adapter
        }

        spinPlatform.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                platformSelection = spinPlatform.selectedItem.toString()
                fetchAndUpdateChats()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        //Set default to players favorite
        mDbRef.child("user").child(mAuth.currentUser!!.uid).child("platform").get()
            .addOnSuccessListener {
                //println("Favorite Game value from db: ${it.value.toString()}")
                for (i in 0 until spinPlatform.count) {
                    if (spinPlatform.getItemAtPosition(i).toString() == it.value.toString()) {
                        spinPlatform.setSelection(i)
                        //println("Game Title Spinner Set to Favorite: $it.value.toString()")
                    }
                }
            }.addOnFailureListener {
                println("piss")
            }

        btnNewChat.setOnClickListener {
            val intent = Intent(this, NewChat::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAndUpdateChats() {
        mDbRef.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentChat = postSnapshot.getValue(ChatObject::class.java)
                    currentChat!!.chatId = postSnapshot.key

                    val formattedDateTime = currentChat.timeCreated
                    if (formattedDateTime != null) {
                        val localDateTime = LocalDateTime.parse(
                            formattedDateTime,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )

                        val chatAge =
                            Duration.between(localDateTime, LocalDateTime.now(ZoneOffset.UTC))
                        if (chatAge.toHours().toInt() > 24) {
                            mDbRef.child("chats").child(currentChat.chatId.toString()).removeValue()
                        }

                        //condition to filter by user choice of Platform(xbox,pc,playstation), Game(COD,Destiny,BungoBros), missing players(chat maximum minus chat current)
                        val playersMissing =
                            currentChat.maxPlayers.minus(currentChat.currentPlayers) //number of players missing
                        if ((playersMissing != null && playersMissing >= playerCountSelection) && (currentChat.gameName == gameSelection) && (currentChat.platform == platformSelection)) {
                            //before valid chat is added, its sortValue is calculated. The default is 999 if something does not work

                            val ageFactor = chatAge.toHours().toFloat() / 24
                            val fullnessFactor =
                                1f - (currentChat.currentPlayers.toFloat() / currentChat.maxPlayers.toFloat())
                            currentChat.sortValue = ageFactor + fullnessFactor //smaller is better.
                            // Values range from 0 - 2, but a completely full chat is not valid
                            // so the maximum score is from a chat that was just created and has a very small percent of their players missing.
                            // for example: chat created 10 minutes ago and it has 29/30 players
                            // the lowest scores will be reserved for chats that have existed for a long time and only have one player

                            if (currentChat.members.size < currentChat.maxPlayers) { //If chat is not full
                                chatList.add(currentChat!!) //add currentChat to array of chats to be displayed only if it matches the filters from the user
                            } else {
                                if (currentChat.members.contains(mAuth.currentUser!!.uid)) { //If user is in chat
                                    chatList.add(currentChat!!) //add currentChat to array of chats to be displayed only if it matches the filters from the user
                                }
                            }
                        }
                    } else {
                        // Handle the case when formattedDateTime is null
                        // For example, show an error message, skip this chat, etc.
                    }


                }


                //Chatlist sort (for zach)

                chatList.sortBy { it.sortValue }


                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
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