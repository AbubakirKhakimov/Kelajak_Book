package com.x.a_technologies.kelajak_book.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.x.a_technologies.kelajak_book.databinding.TermsOfTradeItemLayoutBinding
import com.x.a_technologies.kelajak_book.models.TermsOfTrade

class TermsOfTradeAdapter(val termsList:ArrayList<TermsOfTrade>):RecyclerView.Adapter<TermsOfTradeAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: TermsOfTradeItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(TermsOfTradeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = termsList[position]
        holder.binding.apply {
            title.text = item.title
            mainText.text = item.mainText
        }
    }

    override fun getItemCount(): Int {
        return termsList.size
    }

}