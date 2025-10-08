package com.doctor.eprescription.features.homescreen.patientmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doctor.eprescription.databinding.ItemPatientMenuBinding
import com.doctor.eprescription.databinding.ItemPatientsBinding
import com.doctor.eprescription.domain.model.PatientMenuItem

class PatientMenuAdapter(private val onItemClick: (item: PatientMenuItem) -> Unit) :
    ListAdapter<PatientMenuItem, PatientMenuAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemPatientMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PatientMenuItem) {
            binding.apply {
                ivIcon.setImageResource(item.icon)
                tvName.text = item.name

                root.setOnClickListener { onItemClick(item) }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ItemPatientMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PatientMenuItem>() {
        override fun areItemsTheSame(oldItem: PatientMenuItem, newItem: PatientMenuItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PatientMenuItem,
            newItem: PatientMenuItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}