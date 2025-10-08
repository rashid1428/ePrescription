package com.doctor.eprescription.entrypoint.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.doctor.eprescription.databinding.ItemDrawerBinding
import com.doctor.eprescription.entrypoint.model.DrawerItemModel

class DrawerAdapter : ListAdapter<DrawerItemModel, DrawerAdapter.DrawerViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        ItemDrawerBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return DrawerViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class DrawerViewHolder(val binding: ItemDrawerBinding) : ViewHolder(binding.root) {
        fun bind(item: DrawerItemModel) {
            binding.apply {
                tvText.text = item.name
            }
        }
    }


    class DiffCallBack : DiffUtil.ItemCallback<DrawerItemModel>() {
        override fun areItemsTheSame(oldItem: DrawerItemModel, newItem: DrawerItemModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DrawerItemModel,
            newItem: DrawerItemModel
        ): Boolean {
            return oldItem == newItem
        }

    }


}