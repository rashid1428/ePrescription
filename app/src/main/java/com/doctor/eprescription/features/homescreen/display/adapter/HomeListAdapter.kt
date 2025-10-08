package com.doctor.eprescription.features.homescreen.display.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.doctor.eprescription.R
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.databinding.ItemPatientsBinding

class HomeListAdapter(
    private val onItemClick: (patient: PatientsResponse.Patient) -> Unit,
    private val onEditClick: (patient: PatientsResponse.Patient) -> Unit,
    private val onDeleteClick: (patient: PatientsResponse.Patient) -> Unit,
) : ListAdapter<PatientsResponse.Patient, HomeListAdapter.HomeViewHolder>(DiffCallback()) {

    inner class HomeViewHolder(val binding: ItemPatientsBinding) : ViewHolder(binding.root) {
        fun bind(item: PatientsResponse.Patient) {
            var isMenuShowing = false

            binding.apply {
                tvName.text = item.fullName
                tvDate.text = "DOB ${item.dateOfBirth}"

                ivMenu.setOnClickListener {
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(root)

                    if (isMenuShowing) {
                        isMenuShowing = false

                        constraintSet.connect(
                            R.id.ivMenu,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                        constraintSet.clear(R.id.llButtons, ConstraintSet.END)
                        constraintSet.connect(
                            R.id.llButtons,
                            ConstraintSet.START,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                    } else {
                        isMenuShowing = true

                        constraintSet.connect(
                            R.id.ivMenu,
                            ConstraintSet.END,
                            R.id.llButtons,
                            ConstraintSet.START
                        )

                        constraintSet.clear(R.id.llButtons, ConstraintSet.START)
                        constraintSet.connect(
                            R.id.llButtons,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                    }

                    // Create a transition and set its duration
                    val transition = ChangeBounds()
                    transition.duration = 300 // in milliseconds

                    // Apply the modified constraints to the layout using the transition
                    TransitionManager.beginDelayedTransition(root, transition)

                    constraintSet.applyTo(root)

                    /*llButtons.animate().translationX(0f).apply {
                        duration = 500L
                    }.start()*/
                }

                clPatient.setOnClickListener { onItemClick(getItem(adapterPosition)) }
                ivEdit.setOnClickListener { onEditClick(getItem(adapterPosition)) }
                ivDelete.setOnClickListener { onDeleteClick(getItem(adapterPosition)) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        ItemPatientsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ).also {
            return HomeViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PatientsResponse.Patient>() {
        override fun areItemsTheSame(
            oldItem: PatientsResponse.Patient,
            newItem: PatientsResponse.Patient
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PatientsResponse.Patient,
            newItem: PatientsResponse.Patient
        ): Boolean {
            return oldItem == newItem
        }

    }

}