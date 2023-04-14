package com.example.lfgg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_list_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.memberTextView.text = items[position]
    }

    override fun getItemCount() = items.size

    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberTextView: TextView = view.findViewById(R.id.memberTextView)
    }
}
