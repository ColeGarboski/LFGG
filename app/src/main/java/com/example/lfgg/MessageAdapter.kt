package com.example.lfgg

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var mDbRef: DatabaseReference

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mDbRef = FirebaseDatabase.getInstance().reference

        if(viewType == 1){
            //Inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }else{
            //Inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){
            //Sent View Holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        }else{
            //Received View Holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            //Set chat user to senderId
            mDbRef.child("user").child(currentMessage.senderId!!).child("name").get().addOnSuccessListener {
                holder.receivedMessageUser.text = it.value.toString()
            }.addOnFailureListener {
                holder.receivedMessageUser.text = "Unknown"
            }
            //Set the color of chat's user bubble
            val bubbleTextView = viewHolder.itemView.findViewById<TextView>(R.id.txtUserPicBubble)

            val UID = currentMessage.senderId!!

            val hexColor = usernameToColor(UID)
            val invertedHexColor = invertColor(hexColor)

            val invertedBubbleColor = Color.parseColor(invertedHexColor)
            val bubbleColor = Color.parseColor(hexColor)

            // Calculate color luminance
            val red = Color.red(bubbleColor) / 255.0
            val green = Color.green(bubbleColor) / 255.0
            val blue = Color.blue(bubbleColor) / 255.0
            val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue

            // Set the text color to black or white depending on the luminance
            if (luminance <= 0.5) {
                bubbleTextView.setTextColor(Color.WHITE)
            } else {
                bubbleTextView.setTextColor(Color.BLACK)
            }


            val coloredBubble = setColorToBubble(context, R.drawable.circular_bg, bubbleColor)
            bubbleTextView.background = coloredBubble

            //bubbleTextView.setTextColor(invertedBubbleColor)

            mDbRef.child("user").child(currentMessage.senderId!!).child("name").get().addOnSuccessListener {
                val username = it.value.toString()
                bubbleTextView.text = username.substring(0, 1).uppercase()
                //println(username)
                //println(username.substring(0, 1).uppercase())
            }.addOnFailureListener {
            }
        }
    }

    fun setColorToBubble(context: Context, drawableId: Int, color: Int): GradientDrawable {
        val drawable = ContextCompat.getDrawable(context, drawableId) as GradientDrawable
        drawable.setColor(color)
        return drawable
    }

    fun usernameToColor(username: String): String {
        // Convert the username to a character array
        val chars = username.toCharArray()

        // Compute the hash value
        var hash = 0
        for (char in chars) {
            hash += char.code
        }

        // Use the hash value to determine the color tint (red, green, or blue)
        val colorTint = hash % 3 + 1 // 1 = red, 2 = green, 3 = blue

        // Use the hash and color tint to determine the color components
        var red = 0
        var green = 0
        var blue = 0

        when(colorTint){
            1 -> {
                red = 255
                green = hash % 256
                blue = hash % 20
            }
            2 -> {
                red = hash % 256
                green = 255
                blue = hash % 20
            }
            3 -> {
                red = hash % 256
                green = hash % 20
                blue = 255
            }
            else -> {
                println("bruh");
            }
        }

        // Combine the color components into an ARGB hex string
        val color = StringBuilder("#AABBCCDD")
        color.replace(3, 5, red.toString(16).padStart(2, '0'))
        color.replace(5, 7, green.toString(16).padStart(2, '0'))
        color.replace(7, 9, blue.toString(16).padStart(2, '0'))

        return color.toString()
    }

    fun invertColor(hexColor: String): String {
        val red = hexColor.substring(3, 5).toInt(16)
        val green = hexColor.substring(5, 7).toInt(16)
        val blue = hexColor.substring(7, 9).toInt(16)

        val invertedRed = 255 - red
        val invertedGreen = 255 - green
        val invertedBlue = 255 - blue

        return "#AA${invertedRed.toString(16).padStart(2, '0')}${invertedGreen.toString(16).padStart(2, '0')}${invertedBlue.toString(16).padStart(2, '0')}"
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){

            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receivedMessageUser = itemView.findViewById<TextView>(R.id.txtUsername)
    }
}