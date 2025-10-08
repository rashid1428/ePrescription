package com.doctor.eprescription.features.homescreen.template

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.R
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.constants.ArgsConstant
import com.doctor.eprescription.constants.TemplateTypes
import com.doctor.eprescription.databinding.FragmentTemplateBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.extension.gone
import com.doctor.eprescription.features.homescreen.prescription.details.SelectedMedicinesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TemplateFragment : BaseFragmentVB<FragmentTemplateBinding>(FragmentTemplateBinding::inflate) {

    private val viewModel by viewModels<TemplateViewModel>()

    private val mAdapter by lazy {
        SelectedMedicinesAdapter(onItemClick = { medicineData, optionClicked ->
            viewModel.onMedicineItemViewsClick(medicineData, optionClicked)
        }, onItemChecked = {
            viewModel.onMedicineSelected(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvMedicines.layoutManager = LinearLayoutManager(requireContext())
            rvMedicines.itemAnimator = null
            rvMedicines.adapter = mAdapter

            compBackButton.root.setOnClickListener { findNavController().popBackStack() }

            doneButton.setOnClickListener {
                viewModel.onDoneClick()
            }
            btnAddMore.setOnClickListener {
                viewModel.onAddMore()
            }

            searchView.gone()
        }

        addObservers()
    }


    override fun getMyViewModel() = viewModel

    private fun addObservers() {
        collectWhenStarted {
            viewModel.title.collectLatest { binding.tvTitle.text = it }
        }

        collectWhenStarted {
            viewModel.showDoneButton.collectLatest { binding.doneButton.isVisible = it }
        }

        collectWhenStarted {
            viewModel.showAddMoreButton.collectLatest { binding.btnAddMore.isVisible = it }
        }

        collectWhenStarted {
            viewModel.templateMedicineList.collectLatest {
                mAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    TemplateViewModel.NavigationEvents.NavigateToAddPatient -> {
                        /*findNavController()
                            .navigate(HomeFragmentDirections.actionHomeFragmentToAddEditPatientFragment())*/
                    }

                    is TemplateViewModel.NavigationEvents.NavigateToPatientMenu -> {
                        val bundle = Bundle().apply {
                            putParcelable(ArgsConstant.PATIENT, event.patient)
                        }
                        findNavController().navigate(R.id.patient_menu_graph, bundle)
                    }

                    is TemplateViewModel.NavigationEvents.NavigateToPrescribe -> {
                        val action =
                            TemplateFragmentDirections.actionTemplateFragmentToPrescriptionFragment(
                                event.patient,
                                event.medicineData
                            )
                        findNavController().navigate(action)
                    }

                    is TemplateViewModel.NavigationEvents.NavigateToPrescriptionDetail -> {
                        findNavController().popBackStack()
                        findNavController().navigate(
                            R.id.prescriptionDetailFragment,
                            Bundle().apply {
                                putParcelable("patient", event.patient)
                            })
                    }
                }
            }
        }
    }

}