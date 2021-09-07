package com.jovel.appchangev10.fragments_main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardViewProductsItemBinding
import com.jovel.appchangev10.model.Product
import com.squareup.picasso.Picasso

class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ViewHolder>(){

    private var listProduct : MutableList<Product> = mutableListOf()
    var positionCard = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_products_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listProduct[position])
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    fun appendItems(newItems: MutableList<Product>){
        listProduct.clear()
        listProduct.addAll(newItems)
        notifyDataSetChanged()
    }


    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val binding = CardViewProductsItemBinding.bind(view)
        fun bind(product: Product){
            with(binding){
                description1TextView.text = product.description
                if (product.urlImage != null)
                    Picasso.get().load(product.urlImage).into(productImageView);
            }

        }
    }
}