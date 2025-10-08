package com.doctor.eprescription.features.homescreen.pharmacy.details

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.databinding.FragmentPharmacyDetailsBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.features.homescreen.allergies.details.AllergiesDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PharmacyDetailFragment :
    BaseFragmentVB<FragmentPharmacyDetailsBinding>(FragmentPharmacyDetailsBinding::inflate) {

    private val viewModel by viewModels<PharmacyDetailViewModel>()
    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            /* ivExpandPatientDetails.setOnClickListener {
                 viewModel.onExpandPatientDetailClick()
             }*/

            compBackButton.root.setOnClickListener { findNavController().popBackStack() }

            cvPharmacyDetails.setOnClickListener {
                findNavController().navigate(
                    PharmacyDetailFragmentDirections.actionPharmacyDetailFragmentToPharmacyListFragment(
                        pharmacy = viewModel.pharmacyData.value,
                        patient = viewModel.patientData.value
                    )
                )
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
        collectWhenStarted {
            viewModel.expandPatientDetails.collectLatest {

//                binding.llPatientMoreInfo.isVisible = it
            }
        }
        collectWhenStarted {
            viewModel.pharmacyData.collectLatest {
                it?.let { pharmacy ->
                    with(binding) {
                        tvPharmacyName.text =
                            tvPharmacyName.text.toString().plus(" ").plus(pharmacy.name)
                        tvPharmacySpecialty.text = tvPharmacySpecialty.text.toString().plus(" ")
                            .plus(pharmacy.specialtyName)
                        tvPharmacyAddress.text =
                            tvPharmacyAddress.text.toString().plus(" ").plus(pharmacy.street)
                        tvPharmacyCity.text =
                            tvPharmacyCity.text.toString().plus(" ").plus(pharmacy.city)
                        tvPharmacyState.text =
                            tvPharmacyState.text.toString().plus(" ").plus(pharmacy.state)
                        tvZipCode.text = tvZipCode.text.toString().plus(" ").plus(pharmacy.zipCode)
                    }
                }
//                binding.llPatientMoreInfo.isVisible = it
            }
        }

        collectWhenStarted {
            viewModel.patientData.collectLatest {

            }
        }

        collectWhenStarted {
            viewModel.patientData.collectLatest {
                it?.let { patient ->
                    with(binding) {
                        /*tvPatientName.text = "Name: ${patient.firstName}"
                        val gender = if (patient.gender == " M ") " Male " else " Female "
                        tvPatientGender.text = "Gender: $gender"
                        tvPatientDOB.text = "Date of Birth: ${patient.dateOfBirth}"
                        tvPatientAddress.text = "Address: ${patient.street}"
                        tvPatientCity.text = "City: ${patient.city}"
                        tvPatientState.text = "State: ${patient.state.name}"
                        tvPatientZip.text = "ZipCode: ${patient.zipCode}"
                        tvPatientPhoneNo.text = "Phone No: ${patient.phone}"
                        tvPatientHeight.text = "Height: ${patient.height}"
                        tvPatientWeight.text = "Weight: ${patient.weight}"

                        binding.ivPatient.setImageResource(
                            if (patient.gender == "M") R.drawable.male_placeholder
                            else R.drawable.female_placeholder
                        )*/
                    }
                }
            }
        }
    }

}