package com.doctor.eprescription.features.homescreen.prescription.detaildialog

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.CancelErxRequest
import com.doctor.eprescription.data.remote.model.request.GetPrescriptionDetailRequest
import com.doctor.eprescription.data.remote.model.response.GetPrescriptionDetailResponse
import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionDetailDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cacheRepo: CacheRepo,
    private val patientRepo: PatientRepo
) : BaseViewModel() {

    private val patient = savedStateHandle.get<PatientsResponse.Patient?>("patient")
    private val pharmacy = savedStateHandle.get<Pharmacy?>("pharmacy")
    private val medicine =
        savedStateHandle.get<GetSessionPrescriptionResponse.Medicine?>("medicine")

    private val _prescriptionDetailResponse = MutableStateFlow<GetPrescriptionDetailResponse?>(null)
    val prescriptionDetailResponse = _prescriptionDetailResponse.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    init {
        getPrescriptionDetails()
    }

    private fun getPrescriptionDetails() {
        viewModelScope.launch {
            showLoader()
            val loginData = cacheRepo.getLoginResponse().first()
            val request = GetPrescriptionDetailRequest(
                doctorId = loginData?.data?.id ?: 0,
                patientId = patient?.patientId.toString(),
                messageId = medicine?.messageId.orEmpty(),
                prescriptionMedicineId = medicine?.prescriptionMedicineId.toString()
            )
            when (val call = patientRepo.getPrescriptionDetails(request)) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    _prescriptionDetailResponse.emit(call.data)
                }
            }
        }
    }

    fun cancelErx() {
        viewModelScope.launch {
            showLoader()
            val loginData = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.cancelErx(
                CancelErxRequest(
                    doctorId = loginData?.data?.id ?: 0,
                    patientId = patient?.patientId ?: 0,
                    pharmacyId = pharmacy?.id.toString(),
                    prescriptionId = medicine?.prescriptionId ?: 0,
                    medicines = listOf(_prescriptionDetailResponse.value?.data?.renewalRequestDetailData?.prescribedMedication?.medicineId ?: 0)
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _channel.send(NavigationEvents.ErxCancelSuccess)
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    sealed class NavigationEvents {
        object ErxCancelSuccess : NavigationEvents()
    }

}