package com.doctor.eprescription.features.homescreen.allergies.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.components.listselection.ListSelectionDialog
import com.doctor.eprescription.databinding.FragmentAllergiesDetailsBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllergiesDetailFragment :
    BaseFragmentVB<FragmentAllergiesDetailsBinding>(FragmentAllergiesDetailsBinding::inflate) {

    private val viewModel by viewModels<AllergiesDetailViewModel>()
    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            /* ivExpandPatientDetails.setOnClickListener {
                 viewModel.onExpandPatientDetailClick()
             }*/

            compBackButton.root.setOnClickListener { findNavController().popBackStack() }
            cvPharmacyDetails.setOnClickListener { viewModel.onFoodAllergiesClick() }
        }

        addObservers()
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.expandPatientDetails.collectLatest {

//                binding.llPatientMoreInfo.isVisible = it
            }
        }
        collectWhenStarted {
            viewModel.allergies.collectLatest { foodAllergies ->
                if (foodAllergies.isNotEmpty()) {
                    with(binding) {
                        tvAllergies.text = foodAllergies
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is AllergiesDetailViewModel.NavigationEvents.ShowFoodAllergiesDialog -> {
                        ListSelectionDialog.showForMultiSelection(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItems,
                            heading = "Food And Drug Allergies",
                           onItemsSelected = {
                               viewModel.onFoodAndAllergiesUpdate(it)
                           }
                        )
                    }
                }
            }
        }

    }

}