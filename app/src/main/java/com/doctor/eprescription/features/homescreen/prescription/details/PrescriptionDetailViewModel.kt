package com.doctor.eprescription.features.homescreen.prescription.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.constants.MedicineItemOption
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.DeletePendingMedicineRequest
import com.doctor.eprescription.data.remote.model.request.EcpsVerificationRequest
import com.doctor.eprescription.data.remote.model.request.GetPharmacyRequest
import com.doctor.eprescription.data.remote.model.request.GetRouteListRequest
import com.doctor.eprescription.data.remote.model.request.UpdatePharmacyRequest
import com.doctor.eprescription.data.remote.model.response.*
import com.doctor.eprescription.data.remote.model.response.common.MedicineData
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
class PrescriptionDetailViewModel @Inject constructor(
    val patientRepo: PatientRepo, val cacheRepo: CacheRepo, savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _patientData =
        MutableStateFlow(savedStateHandle.get<PatientsResponse.Patient>("patient"))
    val patientData = _patientData.asStateFlow()

    private val _pharmacy = MutableStateFlow<Pharmacy?>(savedStateHandle["pharmacy"])
    val pharmacy = _pharmacy.asStateFlow()
    private val pharmacies = mutableListOf<Pharmacy>()

    private val _prescriber = MutableStateFlow<LoginResponse?>(null)
    val prescriber = _prescriber.asStateFlow()

    private val _expandPatientDetails = MutableStateFlow(false)
    val expandPatientDetails = _expandPatientDetails.asStateFlow()

    private val _pendingMedicine = MutableStateFlow<List<MedicineData>>(emptyList())
    val pendingMedicine = _pendingMedicine.asStateFlow()

    private val _showControlSubstanceView = MutableStateFlow<Boolean>(false)
    val showControlSubstanceView = _showControlSubstanceView.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    init {
        getPendingMedicines()
    }

    private fun getPendingMedicines() {
        viewModelScope.launch {
            showLoader()
            val loginData = cacheRepo.getLoginResponse().first()
            _prescriber.emit(loginData)
            val patientId = patientData.value?.patientId

            when (val call = patientRepo.getPendingMedicine(
                GetRouteListRequest(
                    patientId = patientId.toString(), doctorId = loginData?.data?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    call.data.data?.let {
                        _pendingMedicine.emit(it.medicinesList)
                    }
                }
            }
        }
    }

    private fun deletePendingMedicines(pendingMedicineId: Int) {
        viewModelScope.launch {
            showLoader()
            val loginData = cacheRepo.getLoginResponse().first()
            val patientId = patientData.value?.patientId

            when (val call = patientRepo.deletePendingMedicine(
                DeletePendingMedicineRequest(
//                    patientId = patientId,
//                    doctorId = loginData?.data?.id,
                    medicineId = listOf(pendingMedicineId)
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _pendingMedicine.value.find { it.pendingMedicineId == pendingMedicineId }
                            ?.let {
                                val newList = _pendingMedicine.value.minusElement(it)
                                _pendingMedicine.emit(newList)
                            }
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    fun onExpandPatientDetailClick() {
        viewModelScope.launch {
            _expandPatientDetails.emit(_expandPatientDetails.value.not())
        }
    }

    fun onAddMoreButtonClick() {
        viewModelScope.launch {
            _channel.send(NavigationEvents.NavigateToPrescriptionDetail(patientData.value))
        }
    }

    fun onMedicineItemViewsClick(item: MedicineData, optionClicked: Int) {
        viewModelScope.launch {
            when (optionClicked) {
                MedicineItemOption.MEDICINE_ITEM -> {
//                    _channel.send(PatientMenuViewModel.NavigationEvents.NavigateToPrescription(patientData.value!!))
                }

                MedicineItemOption.EDIT_BUTTON -> {
                    _channel.send(NavigationEvents.NavigateToPrescribe(patientData.value, item))
                }

                MedicineItemOption.DELETE_BUTTON -> {
                    deletePendingMedicines(item.pendingMedicineId)
                }
            }
        }
    }

    fun onSendClick(
        signingPhrase: String, tokenPin: String
    ) {
        viewModelScope.launch {
            if (_pendingMedicine.value.isEmpty()) return@launch

            if (_showControlSubstanceView.value) {
                if (signingPhrase.isEmpty() || tokenPin.isEmpty()) {
                    showError(ErrorModel("Error", "Signing Phrase and Token Pin is Required"))
                    return@launch
                }

                ecpsVerification(signingPhrase, tokenPin) {
                    addPendingMedicine()
                }
            }

            if (_pendingMedicine.value.any { it.isControlledSubstance == 1 }) {
                _showControlSubstanceView.emit(true)
                return@launch
            }

            addPendingMedicine()
        }
    }

    private suspend fun ecpsVerification(
        signingPhrase: String,
        tokenPin: String,
        onSuccess: suspend () -> Unit
    ) {
        showLoader()
        when (val call = patientRepo.ecpsVerification(
            EcpsVerificationRequest(
                credentialId = "",
                code = tokenPin,
                pass = signingPhrase
            )
        )) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                onSuccess()
            }
        }
    }

    private suspend fun addPendingMedicine() {
        showLoader()
        val doctorData = cacheRepo.getLoginResponse().first()
        val request = SendMedicineRequest(
            doctorId = doctorData?.data?.id ?: 0,
            patientId = patientData.value?.patientId.toString(),
            pharmacyId = pharmacy.value?.id.toString(),
            controlledSubstanceExists = null,
            verified = null,
            //                medicines = pendingMedicine.value
        )
        when (val call = patientRepo.sendMedicine(request)) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                showSuccess(ErrorModel("Success", "Prescription send successfully"))
            }
        }
    }

    fun onEditPatientClick() {
        viewModelScope.launch {
            _channel.send(NavigationEvents.NavigateToEditPatient(_patientData.value))
        }
    }

    fun onRefresh() {
        getPendingMedicines()
    }

    fun onUpdatePatientData(patient: PatientsResponse.Patient?) {
        viewModelScope.launch {
            _patientData.emit(patient)
        }
    }

    private suspend fun getPharmacies(onGet: suspend (List<Pharmacy>) -> Unit) {
        if (pharmacies.isNotEmpty()) {
            onGet(pharmacies)
            return
        }

        showLoader()
        when (val call = patientRepo.getPharmacy(GetPharmacyRequest())) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                if (call.data.callStatus == "true") {
                    pharmacies.clear()
                    pharmacies.addAll(call.data.data.pharmacyData)
                    onGet(call.data.data.pharmacyData)
                } else {
                    showError(ErrorModel("Error", call.data.resultDesc))
                }
            }
        }
    }

    fun onEditPharmacyClick() {
        viewModelScope.launch {
            _channel.send(
                NavigationEvents.NavigateToPharmacy(
                    pharmacy.value
                )
            )
        }
    }

    fun onPharmacyChanged(selectedPharmacy: Pharmacy?) {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.updatePharmacy(
                UpdatePharmacyRequest(
                    patientId = patientData.value?.patientId.toString(),
                    pharmacyId = selectedPharmacy?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        selectedPharmacy?.let { _pharmacy.emit(it) }
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    sealed class NavigationEvents {
        data class NavigateToPrescriptionDetail(
            val patient: PatientsResponse.Patient?
        ) : NavigationEvents()

        data class NavigateToPrescribe(
            val patient: PatientsResponse.Patient?, val medicineData: MedicineData
        ) : NavigationEvents()

        data class NavigateToEditPatient(
            val patient: PatientsResponse.Patient?
        ) : NavigationEvents()

        data class NavigateToPharmacy(
            val selectedPharmacy: Pharmacy?
        ) : NavigationEvents()

        object NavigateToMenu : NavigationEvents()
    }
}