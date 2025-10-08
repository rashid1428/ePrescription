package com.doctor.eprescription.features.homescreen.prescription.createprescription

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doctor.eprescription.R
import com.doctor.eprescription.databinding.ItemCompoundingBinding
import com.doctor.eprescription.extension.color
import com.doctor.eprescription.features.homescreen.prescription.createprescription.model.CompoundingModel

class CompoundingAdapter(
    val onAddClick: () -> Unit,
    val onRemoveClick: (position: Int) -> Unit
) : ListAdapter<CompoundingModel, CompoundingAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ItemCompoundingBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemCompoundingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CompoundingModel) {
            binding.apply {
                fetMedicines.setText(item.medicine)
                fetQuantity.setText(item.quantity.toString())
                fetQuantityUnit.setText(item.quantityUnit)

                fetMedicines.actAsClickItem {

                }

                fetQuantityUnit.actAsClickItem {

                }

                btnAction.setOnClickListener {
                    if (adapterPosition == 0) {
                        onAddClick()
                    } else {
                        onRemoveClick(adapterPosition)
                    }
                }

                if (adapterPosition == 0) {
                    btnAction.setImageResource(R.drawable.ic_add)
                    btnAction.backgroundTintList =
                        ColorStateList.valueOf(this.root.context.color(R.color.blue))
                } else {
                    btnAction.setImageResource(R.drawable.baseline_delete_24)
                    btnAction.backgroundTintList =
                        ColorStateList.valueOf(this.root.context.color(R.color.red))
                }
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<CompoundingModel>() {
        override fun areItemsTheSame(
            oldItem: CompoundingModel,
            newItem: CompoundingModel
        ): Boolean {
            return oldItem.id== newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CompoundingModel,
            newItem: CompoundingModel
        ): Boolean {
            return oldItem.id  == newItem.id
        }

    }
}