package com.x.a_technologies.kelajak_book.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.BookSearchItemLayoutBinding
import com.x.a_technologies.kelajak_book.models.Book

interface TopBookSearchCallBack{
    fun topBookSearchItemClickListener(position: Int)
}

class TopBookSearchAdapter(val searchBooksList:ArrayList<Book>, val topBookSearchCallBack: TopBookSearchCallBack, val context:Context)
    : RecyclerView.Adapter<TopBookSearchAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: BookSearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(BookSearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = searchBooksList[position]

        holder.binding.bookName.text = item.name
        holder.binding.bookAuthorName.text = item.author
        Glide.with(holder.binding.root).load(item.imageUrl).into(holder.binding.bookImage)
        holder.binding.isAvailable.text = getAvailableText(item.count)
        holder.binding.isAvailable.setBackgroundColor(getAvailableColor(item.count))

        holder.binding.itemLayoutRoot.setOnClickListener {
            topBookSearchCallBack.topBookSearchItemClickListener(position)
        }

    }

    override fun getItemCount(): Int {
        return searchBooksList.size
    }

    private fun getAvailableText(count: Int):String{
        return if (count == 0){
            context.getString(R.string.notAvailable)
        }else{
            context.getString(R.string.available)
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