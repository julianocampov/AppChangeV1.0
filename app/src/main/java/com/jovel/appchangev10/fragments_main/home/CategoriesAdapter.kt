package com.jovel.appchangev10.fragments_main.home

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardviewCategoriesBinding
import com.jovel.appchangev10.model.Category
import com.squareup.picasso.Picasso

class CategoriesAdapter (
    private val onItemClicked : (Category) -> Unit,
        ) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var listCategories : MutableList<Category> = mutableListOf()
    var positionSelect = 0

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_categories, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCategories[position])

        holder.card.setOnClickListener {
            positionSelect=position
            onItemClicked(listCategories[position])
            notifyDataSetChanged()
        }

        if(position == positionSelect)
            holder.text.setTextColor(Color.GRAY)
        else
            holder.text.setTextColor(Color.BLACK)

    }

    override fun getItemCount(): Int {
        return  listCategories.size
    }

    fun appendItems(newItems: MutableList<Category>){
        listCategories.clear()
        listCategories.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardviewCategoriesBinding.bind(view)

        val card = binding.categoryCardView
        val text = binding.categoriesTextView

        fun bind(category: Category) {
            binding.categoriesTextView.text = category.name
            if(category.urlImage != null){
                Picasso.get().load(category.urlImage).into(binding.iconImageView)
            }
        }
    }
}