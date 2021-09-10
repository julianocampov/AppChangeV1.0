package com.jovel.appchangev10.fragments_main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentProductBinding
import com.squareup.picasso.Picasso

class ProductFragment : Fragment() {

    private lateinit var productBinding: FragmentProductBinding

    private val args : ProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)

        val product = args.product

        productBinding.descriptionTextView.text = product.title

        if (product.urlImage != null)
            Picasso.get().load(product.urlImage).into(productBinding.profileOwnerImageView)
            Picasso.get().load(product.urlImage).into(productBinding.productImgImageView)

        return productBinding.root
    }
}