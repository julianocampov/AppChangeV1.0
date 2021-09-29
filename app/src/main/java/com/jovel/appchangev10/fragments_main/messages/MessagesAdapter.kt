package com.jovel.appchangev10.fragments_main.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovel.appchangev10.R

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_chat_item, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class Viewholder(view: View) : RecyclerView.ViewHolder(view) {

    }
}