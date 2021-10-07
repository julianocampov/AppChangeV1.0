package com.jovel.appchangev10.fragments_main.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentChatBinding
import com.jovel.appchangev10.model.Chat
import com.jovel.appchangev10.model.Message
import com.jovel.appchangev10.model.User
import com.squareup.picasso.Picasso
import java.util.*

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var chatBinding : FragmentChatBinding
    private var idSecondUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chat = args.chat

        chatBinding = FragmentChatBinding.inflate(inflater, container, false)
        val auth = Firebase.auth
        val db = Firebase.firestore

        loadDataFromArgs(auth, db, chat)

        with(chatBinding){
            toolbar3.setNavigationOnClickListener {
                activity?.onBackPressed()
            }

            messageTextInputLayout.setEndIconOnClickListener {
                val message = messageEditText.text.toString()
                val idOwn = auth.currentUser?.uid

                if (message.isNotEmpty()){
                    val c : Calendar = Calendar.getInstance()

                    val idMessage = c.timeInMillis.toString()
                    val msg = Message(
                        message = message,
                        from = idOwn!!,
                        date = c.timeInMillis.toString())

                    db.collection("users").document(idOwn).collection("chats").document(chat.chatId!!).collection("messages").document(idMessage).set(msg)
                    db.collection("users").document(idSecondUser!!).collection("chats").document(chat.chatId!!).collection("messages").document(idMessage).set(msg)
                }
                messageEditText.setText("")
            }
        }
        return  chatBinding.root
    }

    private fun loadDataFromArgs(auth: FirebaseAuth, db: FirebaseFirestore, chat: Chat) {

        idSecondUser = if (auth.currentUser?.uid != chat.users?.get(0)) {
            chat.users?.get(0)
        }else {
            chat.users?.get(1)
        }

        db.collection("users").document(idSecondUser!!).get()
            .addOnSuccessListener {
                var user: User = it.toObject()!!

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