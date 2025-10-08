package com.doctor.eprescription.features.homescreen.erxhistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.GetPrescriptionDetailRequest
import com.doctor.eprescription.data.remote.model.request.GetSessionPrescriptionRequest
import com.doctor.eprescription.data.remote.model.response.GetSessionPrescriptionResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ErxHistoryViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    private val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val patient = savedStateHandle.get<PatientsResponse.Patient?>("patient")
    private val pharmacy = savedStateHandle.get<Pharmacy?>("pharmacy")
    private val medicine =
        savedStateHandle.get<GetSessionPrescriptionResponse.Medicine?>("medicine")

    private val medicines: MutableList<GetSessionPrescriptionResponse.Medicine> = mutableListOf()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private val uiEvent = Channel<ErxHistoryIntent>()

    private val _uiState = MutableStateFlow(ErxHistoryState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent()
        getSessionPrescription()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            uiEvent.consumeAsFlow().collectLatest { event ->
                when (event) {
                    is ErxHistoryIntent.OnCancelClick -> {
                        _channel.send(
                            NavigationEvents.NavigateToCancelPrescriptionDetailDialog(
                                title = "RxCancel Request",
                                item = event.item,
                                patient = patient,
                                pharmacy = pharmacy,
                                medicine = medicine
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getSessionPrescription() {
        viewModelScope.launch {
            showLoader()
            val loginData = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.getSessionPrescription(
                GetSessionPrescriptionRequest(
                    doctorId = loginData?.data?.id.toString(),
                    patientId = patient?.patientId.toString(),
                    type = -1
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    medicines.clear()
                    medicines.addAll(call.data.data.medicinesList)

                    medicine?.let { medicine ->
                        _uiState.update { state ->
                            state.copy(erxStatusList = medicines.filter {
                                it.prescriptionId == medicine.prescriptionId
                            })
                        }
                    }
                }
            }
        }
    }

    fun onIntent(intent: ErxHistoryIntent) {
        viewModelScope.launch { uiEvent.send(intent) }
    }

    sealed class NavigationEvents {
        data class NavigateToCancelPrescriptionDetailDialog(
            val title: String,
            val item: GetSessionPrescriptionResponse.Medicine,
            val patient: PatientsResponse.Patient?,
            val pharmacy: Pharmacy?,
            val medicine: GetSessionPrescriptionResponse.Medicine?
        ) : NavigationEvents()
    }
}