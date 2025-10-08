package com.doctor.eprescription.features.homescreen.erxhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse
import com.doctor.eprescription.databinding.ItemErxHistoryBinding
import com.doctor.eprescription.databinding.ItemErxStatusBinding
import com.doctor.eprescription.features.homescreen.erxstatus.ErxStatusAdapter

class ErxHistoryAdapter(private val onButtonClick: (item: GetSessionPrescriptionResponse.Medicine) -> Unit) :
    ListAdapter<GetSessionPrescriptionResponse.Medicine, ErxHistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ItemErxHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemErxHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetSessionPrescriptionResponse.Medicine) {
            binding.apply {
                tvDrugNameValue.text = item.medicineName
                compPrescriber.tvKey.text = "Prescriber"
                compPrescriber.tvValue.text =
                    item.doctorFirstName.plus(" ").plus(item.doctorLastName)

                compPharmacy.tvKey.text = "Pharmacy"
                compPharmacy.tvValue.text = item.pharmacyName

                compSig.tvKey.text = "Sig"
                compSig.tvValue.text = item.sig

                compDispense.tvKey.text = "Dispense"
                compDispense.tvValue.text = item.quantityValue.plus(" ").plus(item.quantityUnitOfMeasure)

                compRefill.tvKey.text = "Refill"
                compRefill.tvValue.text = item.numberOfRefills.toString()

                compDate.tvKey.text = "Date"
                compDate.tvValue.text = item.writtenDate

                btnAction.setOnClickListener {
                    onButtonClick(item)
                }

//                root.setOnClickListener { onItemClick(item) }
            }
        }

    }


    class DiffCallback : DiffUtil.ItemCallback<GetSessionPrescriptionResponse.Medicine>() {
        override fun areItemsTheSame(
            oldItem: GetSessionPrescriptionResponse.Medicine,
            newItem: GetSessionPrescriptionResponse.Medicine
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GetSessionPrescriptionResponse.Medicine,
            newItem: GetSessionPrescriptionResponse.Medicine
        ): Boolean {
            return oldItem == newItem
        }

    }
}