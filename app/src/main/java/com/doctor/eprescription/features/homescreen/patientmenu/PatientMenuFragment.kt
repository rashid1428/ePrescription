package com.doctor.eprescription.features.homescreen.patientmenu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.doctor.eprescription.R
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.constants.TemplateTypes
import com.doctor.eprescription.databinding.FragmentPatientMenuBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PatientMenuFragment :
    BaseFragmentVB<FragmentPatientMenuBinding>(FragmentPatientMenuBinding::inflate) {

    private val viewModel by viewModels<PatientMenuViewModel>()

    private val mAdapter by lazy {
        PatientMenuAdapter {
            viewModel.onMenuItemClick(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMenu()

        binding.apply {
            rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)
            rvMenu.adapter = mAdapter

            backer.root.setOnClickListener { findNavController().popBackStack() }
        }

        addObservers()
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.menuItem.collectLatest {
                mAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.patientData.collectLatest {
                it?.let { patient ->
                    binding.tvPatientName.text = it.fullName
                    binding.tvPatientDOB.text = it.dateOfBirth

                    when (patient.gender) {
                        "M" -> binding.ivPatient.setImageResource(R.drawable.male_placeholder)
                        else -> binding.ivPatient.setImageResource(R.drawable.female_placeholder)
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest {
                when (it) {
                    is PatientMenuViewModel.NavigationEvents.NavigateToPrescription -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToPrescriptionDetailFragment(
                                patient = it.patient,
                                pharmacy = it.pharmacy
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToTemplate -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToTemplateFragment(
                                it.patient,
                                TemplateTypes.MY_TEMPLATE
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToMeftiiTemplate -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToTemplateFragment(
                                it.patient,
                                TemplateTypes.MEFTII_TEMPLATE
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToHomeMeds -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToTemplateFragment(
                                it.patient,
                                TemplateTypes.HOME_MEDS
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToOrganizationTemplate -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToTemplateFragment(
                                it.patient,
                                TemplateTypes.ORGANIZATION_TEMPLATE
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToPharmacy -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToPharmacyDetailFragment(
                                it.pharmacy
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToAllergies -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToAllergiesDetailFragment(
                                it.patient,
                                it.allergiesData
                            )
                        findNavController().navigate(action)
                    }

                    is PatientMenuViewModel.NavigationEvents.NavigateToErxStatus -> {
                        val action =
                            PatientMenuFragmentDirections.actionPatientMenuFragmentToErxHistoryFragment(
                                it.patient,
                                it.pharmacy
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun getMyViewModel() = viewModel
}