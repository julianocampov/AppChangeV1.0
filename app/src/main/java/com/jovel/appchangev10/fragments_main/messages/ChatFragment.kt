package com.jovel.appchangev10.fragments_main.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentChatBinding
import com.jovel.appchangev10.model.Message
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso
import java.util.*

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var chatBinding : FragmentChatBinding
    private var idSecondUser: String? = null
    private lateinit var messagesAdapter : MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        chatBinding = FragmentChatBinding.inflate(inflater, container, false)

        messagesAdapter = MessagesAdapter()
        chatBinding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatFragment.context, RecyclerView.VERTICAL, false)
            adapter = messagesAdapter
            setHasFixedSize(false)
        }

        loadUserDataFromArgs()
        loadMessagesFromFB()
        buttonsListeners()
        messagesListener()

        return  chatBinding.root
    }

    private fun messagesListener() {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val chat = args.chat
        val idOwn = auth.currentUser?.uid

        val listMessage: MutableList<Message> = arrayListOf()

        db.collection("users").document(idOwn!!)
            .collection("chats").document(chat.chatId!!)
            .collection("messages")
            .addSnapshotListener{ result, error ->
                if (error == null){
                    if (result != null) {
                        listMessage.clear()
                        for (document in result){
                            val msg : Message = document.toObject()
                            listMessage.add(msg)
                        }
                    }
                    messagesAdapter.appendItems(listMessage)
                    chatBinding.messagesRecyclerView.scrollToPosition(listMessage.size-1)
                }
            }
    }

    private fun loadMessagesFromFB() {

        val auth = Firebase.auth
        val db = Firebase.firestore
        val chat = args.chat
        val idOwn = auth.currentUser?.uid

        val listMessage: MutableList<Message> = arrayListOf()

        db.collection("users").document(idOwn!!)
            .collection("chats").document(chat.chatId!!)
            .collection("messages").get().addOnSuccessListener { result ->
            for (document in result){
                val msg : Message = document.toObject()
                listMessage.add(msg)
            }
            messagesAdapter.appendItems(listMessage)
            chatBinding.messagesRecyclerView.scrollToPosition(listMessage.size-1)
        }
    }

    private fun buttonsListeners() {
        with(chatBinding){
            toolbar3.setNavigationOnClickListener {
                activity?.onBackPressed()
            }

            messageTextInputLayout.setEndIconOnClickListener {
                sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val chat = args.chat

        val message = chatBinding.messageEditText.text.toString()
        val idOwn = auth.currentUser?.uid

        if (message.isNotEmpty()){
            val c : Calendar = Calendar.getInstance()

            val idMessage = c.timeInMillis.toString()
            val msg = Message(
                message = message,
                from = idOwn!!,
                date = c.timeInMillis.toString())

            db.collection("users").document(idOwn).collection("chats").document(chat.chatId!!).collection("messages").document(idMessage).set(msg)
            db.collection("users").document(idSecondUser!!).collection("chats").document(chat.chatId).collection("messages").document(idMessage).set(msg)
            db.collection("users").document(idSecondUser!!).collection("chats").document(chat.chatId).set(chat)
        }
        chatBinding.messageEditText.setText("")
        loadMessagesFromFB()
    }

    private fun loadUserDataFromArgs() {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val chat = args.chat

        idSecondUser = if (auth.currentUser?.uid != chat.users?.get(0)) {
            chat.users?.get(0)
        }else {
            chat.users?.get(1)
        }

        db.collection("users").document(idSecondUser!!).get()
            .addOnSuccessListener {
                val user: User = it.toObject()!!

                with(chatBinding){
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