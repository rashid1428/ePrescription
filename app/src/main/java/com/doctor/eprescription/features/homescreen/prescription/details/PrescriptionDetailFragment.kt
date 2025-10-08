package com.doctor.eprescription.features.homescreen.prescription.details

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.R
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.components.listselection.ListSelectionDialog
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.databinding.FragmentPrescriptionDetailsBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PrescriptionDetailFragment :
    BaseFragmentVB<FragmentPrescriptionDetailsBinding>(FragmentPrescriptionDetailsBinding::inflate) {

    private val viewModel by viewModels<PrescriptionDetailViewModel>()
    override fun getMyViewModel() = viewModel

    private val mAdapter by lazy {
        SelectedMedicinesAdapter(onItemClick = { medicineData, optionClicked ->
            viewModel.onMedicineItemViewsClick(medicineData, optionClicked)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            backer.root.setOnClickListener { findNavController().popBackStack() }

            ivExpandPatientDetails.setOnClickListener {
                viewModel.onExpandPatientDetailClick()
            }
            btnAddMore.setOnClickListener {
                viewModel.onAddMoreButtonClick()
            }

            btnSend.setOnClickListener {
                viewModel.onSendClick(
                    etSigningPhrase.text.toString(),
                    etTokenPin.text.toString()
                )
            }

            btnEditPatient.setOnClickListener {
                viewModel.onEditPatientClick()
            }

            btnEditPharmacy.setOnClickListener { viewModel.onEditPharmacyClick() }

            rvSelectedMedicines.layoutManager = LinearLayoutManager(requireContext())
            rvSelectedMedicines.adapter = mAdapter
        }

        setFragmentResultListener("refresh") { requestKey, bundle ->
            if (bundle.getBoolean("refresh")) {
                viewModel.onRefresh()
            }
        }

        setFragmentResultListener("pharmacy_updated") { requestKey, bundle ->
            val pharmacy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("pharmacy", Pharmacy::class.java)
            } else {
                bundle.getParcelable("pharmacy")
            }

            viewModel.onPharmacyChanged(pharmacy)
        }

        addObservers()
    }

    private fun addObservers() {
        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<PatientsResponse.Patient>("patient")
            ?.observe(viewLifecycleOwner) {
                viewModel.onUpdatePatientData(it)
            }

        collectWhenStarted {
            viewModel.expandPatientDetails.collectLatest {
                binding.ivExpandPatientDetails.rotation = if (it) 0f else 180f
                binding.llPatientMoreInfo.isVisible = it
            }
        }

        collectWhenStarted {
            viewModel.showControlSubstanceView.collectLatest {
                binding.apply {
                    llControlSubstance.isVisible = it
                    if (it) {
                        scrollView.postDelayed({
                            scrollView.scrollTo(0, scrollView.bottom)
                        }, 200)
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.pendingMedicine.collectLatest {
                mAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.prescriber.collectLatest {
                binding.tvPrescriberName.text = "Name: ${it?.data?.fullName}"
                binding.tvPrescriberGender.text = "Gender: ${it?.data?.gender}"
                binding.tvPrescriberDob.text = "DOB: ${it?.data?.dateOfBirth}"
            }
        }

        collectWhenStarted {
            viewModel.patientData.collectLatest {
                it?.let { patient ->
                    with(binding) {
                        tvPatientName.text = "Name: ${patient.fullName}"
                        val gender = if (patient.gender == "M") " Male " else " Female "
                        tvPatientGender.text = "Gender: $gender"
                        tvPatientDOB.text = "Date of Birth: ${patient.dateOfBirth}"
                        tvPatientAddress.text = "Address: ${patient.street}"
                        tvPatientCity.text = "City: ${patient.city}"
                        tvPatientState.text = "State: ${patient.state.name}"
                        tvPatientZip.text = "ZipCode: ${patient.zipCode}"
                        tvPatientPhoneNo.text = "Phone No: ${patient.phone}"
                        tvPatientHeight.text = "Height: ${patient.height}"
                        tvPatientWeight.text = "Weight: ${patient.weight}"

                        ivPatient.setImageResource(
                            if (patient.gender == "M") R.drawable.male_placeholder
                            else R.drawable.female_placeholder
                        )
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.pharmacy.collectLatest {
                it?.let { pharmacy ->
                    binding.tvPharmacyName.text = "Name: ${pharmacy.name}"
                    binding.tvPharmacyAddress.text = "Address: ${pharmacy.street}"
                    binding.tvPharmacyCity.text = "City: ${pharmacy.city}"
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is PrescriptionDetailViewModel.NavigationEvents.NavigateToPrescriptionDetail -> {
                        val action =
                            PrescriptionDetailFragmentDirections.actionPrescriptionDetailFragmentToPrescriptionFragment(
                                event.patient
                            )
                        findNavController().navigate(action)
                    }

                    is PrescriptionDetailViewModel.NavigationEvents.NavigateToPrescribe -> {
                        if (event.patient?.gender?.isNotEmpty() == true) {

                        }
                        val action =
                            PrescriptionDetailFragmentDirections.actionPrescriptionDetailFragmentToPrescriptionFragment(
                                event.patient,
                                event.medicineData
                            )
                        findNavController().navigate(action, navOptions { launchSingleTop = true })
                    }

                    PrescriptionDetailViewModel.NavigationEvents.NavigateToMenu -> {
                        findNavController().popBackStack(R.id.patientMenuFragment, false)
                        /*findNavController().navigate(R.id.patientMenuFragment, null, navOptions {
                            this.popUpTo(R.id.patientMenuFragment, popUpToBuilder = {
                                this.inclusive = false
                            })
                        })*/
                    }

                    is PrescriptionDetailViewModel.NavigationEvents.NavigateToEditPatient -> {
                        findNavController().navigate(R.id.addEditPatientFragment, Bundle().apply {
                            putParcelable("patient", event.patient)
                        })
                    }

                    is PrescriptionDetailViewModel.NavigationEvents.NavigateToPharmacy -> {
                        //if pharmacy object is passed then this screen will behaves as selection
                        findNavController().navigate(
                            PrescriptionDetailFragmentDirections.actionPrescriptionDetailFragmentToPharmacyListFragment(
                                event.selectedPharmacy
                            )
                        )
                    }
                }
            }
        }
    }

}