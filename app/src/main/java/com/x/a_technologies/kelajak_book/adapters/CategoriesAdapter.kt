package com.x.a_technologies.kelajak_book.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.CategoriesItemLayoutBinding

interface CategoriesCallBack{
    fun categoriesItemClickListener(position: Int)
}

class CategoriesAdapter(val context: Context, val categoriesList:ArrayList<String>, val categoriesCallBack: CategoriesCallBack)
    :RecyclerView.Adapter<CategoriesAdapter.ItemHolder>() {

    inner class ItemHolder(val binding: CategoriesItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    var selectedItemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(CategoriesItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.binding.categoryName.text = categoriesList[position]

        if (selectedItemPosition == position){
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.main_color))
            holder.binding.categoryName.setTextColor(Color.WHITE)
        }else{
            holder.binding.cardView.setCardBackgroundColor(Color.WHITE)
            holder.binding.categoryName.setTextColor(ContextCompat.getColor(context, R.color.main_color))
        }

        holder.binding.cardView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()

            categoriesCallBack.categoriesItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }
}