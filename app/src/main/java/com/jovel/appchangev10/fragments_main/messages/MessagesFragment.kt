package com.jovel.appchangev10.fragments_main.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.databinding.FragmentMessagesBinding
import com.jovel.appchangev10.model.Chat

class MessagesFragment : Fragment() {

    private lateinit var messagesBinding: FragmentMessagesBinding
    private lateinit var chatsAdapter: ChatsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        messagesBinding = FragmentMessagesBinding.inflate(inflater, container, false)

        chatsAdapter = ChatsAdapter (onMessageClicked = {onMessageClicked(it)})
        messagesBinding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessagesFragment.context, RecyclerView.VERTICAL, false)
            adapter = chatsAdapter
            setHasFixedSize(false)
        }

        loadFromFB()
        return messagesBinding.root
    }

    private fun loadFromFB() {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val id = auth.currentUser?.uid
        val listChat : MutableList<Chat> = arrayListOf()

        db.collection("users").document(id!!).collection("chats").get().addOnSuccessListener {
            for (document in it){
                val chat : Chat = document.toObject()
                listChat.add(chat)
            }
            chatsAdapter.appendItems(listChat)
        }
    }

    private fun onMessageClicked(chat: Chat) {
        findNavController().navigate(MessagesFragmentDirections.actionNavigationMessagesToChatFragment(chat))
    }
}