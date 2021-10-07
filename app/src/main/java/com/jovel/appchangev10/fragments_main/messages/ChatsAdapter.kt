package com.jovel.appchangev10.fragments_main.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardViewChatItemBinding
import com.jovel.appchangev10.model.Chat
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso

class ChatsAdapter (
    private val onMessageClicked : (Chat) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private val listMessage : MutableList<Chat> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMessage[position])
        holder.itemView.setOnClickListener{ onMessageClicked(listMessage[position])}
    }

    override fun getItemCount(): Int = listMessage.size

    fun appendItems(newItems: MutableList<Chat>){
        listMessage.clear()
        listMessage.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = CardViewChatItemBinding.bind(view)
        private lateinit var user: User

        fun bind(chat: Chat) {

            val auth = Firebase.auth
            val db = Firebase.firestore

            val id : String? = if (auth.currentUser?.uid != chat.users?.get(0)){
                chat.users?.get(0)
            } else{
                chat.users?.get(1)
            }

            db.collection("users").document(id!!).get()
                .addOnSuccessListener {
                    user = it.toObject()!!

                    with(binding){
                        nameChatTextView.text = user.name

                        if (user.urlProfileImage != null) {
                            Picasso.get().load(user.urlProfileImage)
                                .into(profileChatImageView)
                        }else{
                            Picasso.get().load(R.drawable.not_picture)
                                .into(profileChatImageView)
                        }
                    }
                }
        }
    }
}