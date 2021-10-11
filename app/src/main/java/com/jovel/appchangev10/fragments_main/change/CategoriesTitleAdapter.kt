package com.jovel.appchangev10.fragments_main.home

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.CardviewCategoriesNoIconBinding
import com.jovel.appchangev10.model.Category

class CategoriesTitleAdapter(
    private val onItemClicked: (Category) -> Unit,
) : RecyclerView.Adapter<CategoriesTitleAdapter.ViewHolder>() {

    private var listCategories: MutableList<Category> = mutableListOf()
    private var isSelected = true
    private lateinit var ctx: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_categories_no_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCategories[position])

        holder.card.setOnClickListener {
            onItemClicked(listCategories[position])
            notifyDataSetChanged()
        }

        if (isSelected) {
            holder.text.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_app))
            holder.text.setTextColor(ContextCompat.getColor(ctx, R.color.white))
        } else {
            holder.text.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
            holder.text.setTextColor(ContextCompat.getColor(ctx, R.color.color_app))
        }
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    fun appendItems(newItems: MutableList<Category>) {
        listCategories.clear()
        listCategories.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setBan(isSelect: Boolean) {
        isSelected = isSelect
    }

    fun setContext(context: Context) {
        ctx = context
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