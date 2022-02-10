package com.x.a_technologies.kelajak_book.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.kelajak_book.databinding.BooksByCategoriesItemLayoutBinding
import com.x.a_technologies.kelajak_book.models.Book

interface BooksByCategoriesCallBack{
    fun booksByCategoriesItemClickListener(position: Int)
}

class BooksByCategoriesAdapter(val booksList:ArrayList<Book>, val booksByCategoriesCallBack: BooksByCategoriesCallBack)
    : RecyclerView.Adapter<BooksByCategoriesAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: BooksByCategoriesItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
         return ItemHolder(BooksByCategoriesItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = booksList[position]

        holder.binding.bookName.text = item.name
        holder.binding.bookAuthorName.text = item.author
        holder.binding.bookRentPrice.text = item.rentPrice
        holder.binding.bookSellingPrice.text = item.sellingPrice
        Glide.with(holder.binding.root).load(item.imageUrl).into(holder.binding.bookImage)
        holder.binding.isAvailable.text = getAvailableText(item.count)
        holder.binding.isAvailable.setBackgroundColor(getAvailableColor(item.count))

        holder.binding.itemLayoutRoot.setOnClickListener {
            booksByCategoriesCallBack.booksByCategoriesItemClickListener(position)
        }

    }

    override fun getItemCount(): Int {
        return if(booksList.size <= 20){
            booksList.size
        }else{
            20
        }
    }

    private fun getAvailableText(count: Int):String{
        return if (count == 0){
            "Not available"
        }else{
            "Available"
        }
    }

    private fun getAvailableColor(count:Int):Int{
        return when {
            count == 0 -> {
                Color.parseColor("#EA2B1F")
            }
            count <= 3 -> {
                Color.parseColor("#F3B61F")
            }
            count > 3 -> {
                Color.parseColor("#4CAF50")
            }
            else -> Color.parseColor("#4CAF50")
        }
    }

}