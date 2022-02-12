package com.x.a_technologies.kelajak_book.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.ReviewsItemLayoutBinding
import com.x.a_technologies.kelajak_book.models.Review
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewsAdapter(val reviewsList:ArrayList<Review>, val isFullList:Boolean = false):RecyclerView.Adapter<ReviewsAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: ReviewsItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(ReviewsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = reviewsList[position]

        if (item.senderUser.imageUrl == null){
            holder.binding.userImage.setImageResource(R.drawable.user_profile_human)
        }else {
            Glide.with(holder.binding.root).load(item.senderUser.imageUrl)
                .into(holder.binding.userImage)
        }
        holder.binding.userName.text = "${item.senderUser.firstName} ${item.senderUser.lastName}"
        holder.binding.reviewText.text = item.reviewText
        holder.binding.reviewSendDate.text = getDate(item.reviewSendTimeMillis)

        if (item.adminMessage == null){
            holder.binding.adminMessageLayout.visibility = View.GONE
        }else{
            holder.binding.adminMessage.text = item.adminMessage
            holder.binding.adminMessageSendDate.text = getDate(item.adminMessageSendTimeMillis!!)
        }
    }

    override fun getItemCount(): Int {
        return if (isFullList || reviewsList.size <= 3){
            reviewsList.size
        }else{
            3
        }
    }

    private fun getDate(timeMillis:Long):String{
        return SimpleDateFormat("dd MMMM yyyy").format(Date(timeMillis))
    }

}