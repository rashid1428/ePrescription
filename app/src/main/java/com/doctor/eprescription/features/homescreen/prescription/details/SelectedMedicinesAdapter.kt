package com.doctor.eprescription.features.homescreen.prescription.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import com.doctor.eprescription.R
import com.doctor.eprescription.constants.MedicineItemOption
import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.doctor.eprescription.databinding.ItemSelectedMedicineBinding
import com.doctor.eprescription.extension.gone
import com.doctor.eprescription.extension.visible

class SelectedMedicinesAdapter(
    private val onItemClick: (item: MedicineData, optionClicked: Int) -> Unit,
    private val onItemChecked: ((item: MedicineData) -> Unit)? = null,
) : ListAdapter<MedicineData, SelectedMedicinesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ItemSelectedMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .also {
                return ViewHolder(it)
            }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemSelectedMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MedicineData) {
            var isMenuShowing = false

            binding.apply {
                tvMedicineName.text = item.medicineName

                tvDoseQuantity.text = item.dosage
                tvDoseUnit.text = item.doseUnit
                tvRoute.text = item.route
                tvFrequency.text = item.direction
                tvTotalQuantity.text = item.quantity

                tvSig.text = item.combineSig
                tvTotalQuantityUnit.text = item.quantity.plus(" ").plus(item.quantityUnit)
                tvRefill.text = item.refill

                if (onItemChecked != null) {
                    rootLayout.setOnClickListener { onItemChecked.invoke(item) }

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(root)
                    if (item.isSelected) {

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
                        constraintSet.setVisibility(R.id.cbSelected, View.VISIBLE)
                        constraintSet.setVisibility(R.id.ivMenu, View.VISIBLE)
//                            rootLayout.invalidate()
//                        constraintSet.applyTo(root)
                        doTransition(constraintSet)
                    } else {
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
                        constraintSet.setVisibility(R.id.cbSelected, View.GONE)
                        constraintSet.setVisibility(R.id.ivMenu, View.GONE)

//                            rootLayout.invalidate()
//                        constraintSet.applyTo(root)
                            doTransition(constraintSet)
                    }
                } else {
                    cbSelected.gone()
                    rootLayout.setOnClickListener {
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
                        doTransition(constraintSet)
                    }
                }


                /*clMedicine.setOnClickListener {
                    onItemClick(getItem(layoutPosition), MedicineItemOption.MEDICINE_ITEM)
                }*/

                btnEdit.setOnClickListener {
                    onItemClick(getItem(layoutPosition), MedicineItemOption.EDIT_BUTTON)
                }

                btnDelete.setOnClickListener {
                    onItemClick(getItem(layoutPosition), MedicineItemOption.DELETE_BUTTON)
                }
            }

        }

        private fun ItemSelectedMedicineBinding.doTransition(
            constraintSet: ConstraintSet,
            action: (() -> Unit)? = null
        ) {
            val transition = ChangeBounds()
            transition.duration = 300 // in milliseconds

            // Apply the modified constraints to the layout using the transition
            TransitionManager.beginDelayedTransition(root, transition)
            transition.onEnd { action?.invoke() }
            constraintSet.applyTo(root)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MedicineData>() {
        override fun areItemsTheSame(
            oldItem: MedicineData,
            newItem: MedicineData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MedicineData,
            newItem: MedicineData
        ): Boolean {
            return oldItem == newItem
        }

    }
}

fun Transition.onEnd(action: () -> Unit) {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionStart(transition: Transition) {

        }

        override fun onTransitionEnd(transition: Transition) {
            action()
        }

        override fun onTransitionCancel(transition: Transition) {

        }

        override fun onTransitionPause(transition: Transition) {

        }

        override fun onTransitionResume(transition: Transition) {

        }
    })
}