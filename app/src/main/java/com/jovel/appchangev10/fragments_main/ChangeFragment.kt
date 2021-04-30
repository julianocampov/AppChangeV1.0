package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jovel.appchangev10.databinding.FragmentChangeBinding

class ChangeFragment : Fragment() {

    private lateinit var changeBinding: FragmentChangeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        changeBinding = FragmentChangeBinding.inflate(inflater,container, false)
        return changeBinding.root

    }
}