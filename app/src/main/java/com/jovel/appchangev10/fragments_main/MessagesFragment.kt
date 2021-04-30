package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jovel.appchangev10.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment() {

    private lateinit var messagesBinding: FragmentMessagesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        messagesBinding = FragmentMessagesBinding.inflate(inflater, container, false)
        return messagesBinding.root
    }
}