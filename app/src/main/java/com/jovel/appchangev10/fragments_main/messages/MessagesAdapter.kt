package com.jovel.appchangev10.fragments_main.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardviewSimpleMessageBinding
import com.jovel.appchangev10.model.Message

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val listMessages : MutableList<Message> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_simple_message,parent,false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMessages[position])
    }

    override fun getItemCount(): Int = listMessages.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardviewSimpleMessageBinding.bind((view))

        fun bind(message: Message) {

        }

    }
}