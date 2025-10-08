package com.doctor.eprescription.components.listselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doctor.eprescription.databinding.ItemSelectBinding

class SelectionAdapter : ListAdapter<SelectionModel, SelectionAdapter.ViewHolder>(DiffUtils()) {

    private var selectCallback: ((SelectionModel) -> Unit)? = null

    fun setSelectionCallback(callback: (SelectionModel) -> Unit) {
        selectCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ItemSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectionModel) {
            binding.apply {
                tvName.text = item.text
                ivTick.visibility = if (item.isSelected) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

                root.setOnClickListener {
                    selectCallback?.invoke(getItem(adapterPosition))
                }
            }
        }
    }

    class DiffUtils : DiffUtil.ItemCallback<SelectionModel>() {
        override fun areItemsTheSame(oldItem: SelectionModel, newItem: SelectionModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SelectionModel, newItem: SelectionModel): Boolean {
            return oldItem == newItem
        }

    }
}