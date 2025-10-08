package com.doctor.eprescription.features.homescreen.erxstatus

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
class ErxStatusViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    private val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        ErxStatusState(
            tabs = listOf("OnGoing", "Completed", "Failed"),
            tabsSelection = listOf(true, false, false),
        )
    )
    val uiState: StateFlow<ErxStatusState> = _uiState.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private val _uiEvents = Channel<ErxStatusIntent>()

    private val patient = savedStateHandle.get<PatientsResponse.Patient?>("patient")
    private val pharmacy = savedStateHandle.get<Pharmacy?>("pharmacy")
    private val allMedicines: MutableList<GetSessionPrescriptionResponse.Medicine> = mutableListOf()

    init {
        handleIntent()
        getSessionPrescription()
    }

    private fun getSessionPrescription() {
        viewModelScope.launch {
            val loginData = cacheRepo.getLoginResponse().first()
            val doctorId = loginData?.data?.id.toString()
            showLoader()
            val getSessionPrescriptionRequest = GetSessionPrescriptionRequest(
                doctorId = doctorId,
                patientId = patient?.patientId.toString(),
                type = -1
            )

            when (val call = patientRepo.getPrescriptionSession(getSessionPrescriptionRequest)) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    allMedicines.clear()
                    allMedicines.addAll(call.data.data.medicinesList)
                    showCompleted()
                }
            }

        }
    }

    private fun showCompleted() {
        _uiState.update { state ->
            state.copy(erxStatusList = allMedicines.filter { it.statusCode != "900" })
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            _uiEvents.consumeAsFlow().collectLatest { event ->
                when (event) {
                    is ErxStatusIntent.OnTabChanged -> {
                        when (event.position) {
                            0 -> {
                                showCompleted()
                            }
                            1 -> {

                            }
                            2 -> {
                                _uiState.update { state ->
                                    state.copy(erxStatusList = allMedicines.filter { it.statusCode == "900" })
                                }
                            }
                        }
                    }
                    is ErxStatusIntent.OnItemClick -> {
                        _channel.send(
                            NavigationEvents.NavigateToErxHistory(
                                patient = patient,
                                pharmacy = pharmacy,
                                medicine = event.item
                            )
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: ErxStatusIntent) {
        viewModelScope.launch { _uiEvents.send(intent) }
    }

    sealed class NavigationEvents {
        data class NavigateToErxHistory(
            val patient: PatientsResponse.Patient?,
            val pharmacy: Pharmacy?,
            val medicine: GetSessionPrescriptionResponse.Medicine
        ) : NavigationEvents()
    }

}