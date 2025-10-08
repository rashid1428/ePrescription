package com.doctor.eprescription.features.homescreen.prescription.detaildialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.doctor.eprescription.basefragment.FullScreenDialogFragment
import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.databinding.DialogPrescriptionDetailBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PrescriptionDetailDialogFragment :
    FullScreenDialogFragment<DialogPrescriptionDetailBinding>(DialogPrescriptionDetailBinding::inflate) {

    companion object {

        const val ARGS_PATIENT = "patient"
        const val ARGS_PHARMACY = "pharmacy"
        const val ARGS_MEDICINE = "medicine"
        const val ARGS_TITLE = "title"

        fun show(
            fragmentManager: FragmentManager,
            title: String,
            patient: PatientsResponse.Patient?,
            pharmacy: Pharmacy?,
            medicine: GetSessionPrescriptionResponse.Medicine?
        ) {
            val dialog = PrescriptionDetailDialogFragment()
            dialog.arguments = Bundle().apply {
                putString(ARGS_TITLE, title)
                putParcelable(ARGS_PATIENT, patient)
                putParcelable(ARGS_PHARMACY, pharmacy)
                putParcelable(ARGS_MEDICINE, medicine)
            }
            dialog.show(fragmentManager, "PrescriptionDetailDialogFragment")
        }
    }


    private val viewModel by viewModels<PrescriptionDetailDialogViewModel>()

    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()
        setUpClicks()
        addObservers()
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.prescriptionDetailResponse.collectLatest { response ->
                if (response == null) {

                } else {
                    binding.apply {
                        tvPrescriberName.text =
                            response.data.renewalRequestDetailData.previousDoctor.fullName

                        tvPatientName.text =
                            response.data.renewalRequestDetailData.previousPatient.fullName
                        tvAddress.text =
                            response.data.renewalRequestDetailData.previousPatient.address
                        tvCity.text = response.data.renewalRequestDetailData.previousPatient.city
                        tvState.text = response.data.renewalRequestDetailData.previousPatient.state
                        tvZipCode.text =
                            response.data.renewalRequestDetailData.previousPatient.zipCode
                        tvGender.text =
                            response.data.renewalRequestDetailData.previousPatient.zipCode
                        tvDOB.text =
                            response.data.renewalRequestDetailData.previousPatient.dateOfBirth
                        tvHeight.text =
                            response.data.renewalRequestDetailData.previousPatient.height
                        tvWeight.text =
                            response.data.renewalRequestDetailData.previousPatient.weight

                        tvPharmacyName.text =
                            response.data.renewalRequestDetailData.previousPharmacy.name

                        tvDrugDescription.text =
                            response.data.renewalRequestDetailData.prescribedMedication.medicineName
                        tvQuantityUnit.text =
                            response.data.renewalRequestDetailData.prescribedMedication.quantityUnitOfMeasure
                        tvRefill.text =
                            response.data.renewalRequestDetailData.prescribedMedication.numberOfRefills.toString()
                        tvDiagnosis.text = ""
                        tvDaySupply.text =
                            response.data.renewalRequestDetailData.prescribedMedication.daysSupply.toString()
                        tvSubstitution.text =
                            response.data.renewalRequestDetailData.prescribedMedication.substitutions.toString()
                        tvEffectiveDate.text = ""
                        tvSig.text = response.data.renewalRequestDetailData.prescribedMedication.sig
                        tvNotes.text = ""
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    PrescriptionDetailDialogViewModel.NavigationEvents.ErxCancelSuccess -> {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setUpClicks() {
        binding.apply {
            ivClose.setOnClickListener { dismiss() }
            btnAction.setOnClickListener { viewModel.cancelErx() }
        }
    }

    private fun setUpUi() {
        binding.apply {
            tvTitle.text = arguments?.getString(ARGS_TITLE)
        }
    }

}