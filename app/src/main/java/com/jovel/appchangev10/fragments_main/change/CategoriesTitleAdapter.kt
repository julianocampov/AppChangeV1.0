package com.jovel.appchangev10.fragments_main.home

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardviewCategoriesBinding
import com.jovel.appchangev10.databinding.CardviewCategoriesNoIconBinding
import com.jovel.appchangev10.model.Category
import com.squareup.picasso.Picasso

class CategoriesTitleAdapter (
    private val onItemClicked : (Category) -> Unit,
) : RecyclerView.Adapter<CategoriesTitleAdapter.ViewHolder>() {

    private var listCategories : MutableList<Category> = mutableListOf()
    var positionSelect = 999

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_categories_no_icon, parent, false)
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
            holder.text.setTextColor(Color.WHITE)

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
        private val binding = CardviewCategoriesNoIconBinding.bind(view)

        val card = binding.categoryCardView
        val text = binding.categoryTextView

        fun bind(category: Category) {
            binding.categoryTextView.text = category.name
        }
    }

}