package com.doctor.eprescription.features.homescreen.pharmacylist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.databinding.ItemPharmacyListBinding
import com.doctor.eprescription.extension.gone
import com.doctor.eprescription.extension.visible

class PharmacyListAdapter(private val onItemClick: (pharmacy: Pharmacy) -> Unit) :
    ListAdapter<Pharmacy, PharmacyListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPharmacyListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        val size = super.getItemCount()
        Log.e("PharmacyListAdapter", size.toString())
        return super.getItemCount()
    }

    inner class ViewHolder(private val binding: ItemPharmacyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pharmacy: Pharmacy) {
            binding.apply {
                tvPharmacyName.text = "$adapterPosition -> ${pharmacy.name}"
                tvStreet.text = pharmacy.street
                tvCity.text = pharmacy.city
                tvCityCode.text = pharmacy.state
                tvZipCode.text = pharmacy.zipCode

                ivTick.isVisible = pharmacy.isSelected

                root.setOnClickListener {
                    onItemClick(pharmacy)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pharmacy>() {
        override fun areItemsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
            return oldItem == newItem
        }

    }


}