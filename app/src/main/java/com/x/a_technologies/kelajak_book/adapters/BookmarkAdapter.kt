package com.x.a_technologies.kelajak_book.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.BookmarkItemLayoutBinding
import com.x.a_technologies.kelajak_book.databinding.BooksByCategoriesItemLayoutBinding
import com.x.a_technologies.kelajak_book.models.Book

interface BookmarkCallBack{
    fun bookmarkItemClickListener(position: Int)
    fun bookmarkItemRemovedListener(position: Int)
}

class BookmarkAdapter(val booksList:ArrayList<Book>, val bookmarkCallBack: BookmarkCallBack, val context:Context)
    : RecyclerView.Adapter<BookmarkAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: BookmarkItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
         return ItemHolder(BookmarkItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = booksList[position]

        holder.binding.bookName.text = item.name
        holder.binding.bookAuthorName.text = item.author
        holder.binding.bookRentPrice.text = "${item.rentPrice} ${context.getString(R.string.sum)}"
        holder.binding.bookSellingPrice.text = "${item.sellingPrice} ${context.getString(R.string.sum)}"
        Glide.with(holder.binding.root).load(item.imageUrl).into(holder.binding.bookImage)
        holder.binding.isAvailable.text = getAvailableText(item.count)
        holder.binding.isAvailable.setBackgroundColor(getAvailableColor(item.count))

        holder.binding.itemLayoutRoot.setOnClickListener {
            bookmarkCallBack.bookmarkItemClickListener(position)
        }

        holder.binding.deleteBook.setOnClickListener {
            booksList.removeAt(position)
            notifyDataSetChanged()
            bookmarkCallBack.bookmarkItemRemovedListener(position)
        }

    }

    override fun getItemCount(): Int {
        return booksList.size
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