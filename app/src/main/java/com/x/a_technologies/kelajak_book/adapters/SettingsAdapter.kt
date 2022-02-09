package com.x.a_technologies.kelajak_book.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.x.a_technologies.kelajak_book.databinding.SettingsItemLayoutBinding

class SettingsAdapter:RecyclerView.Adapter<SettingsAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: SettingsItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(SettingsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}