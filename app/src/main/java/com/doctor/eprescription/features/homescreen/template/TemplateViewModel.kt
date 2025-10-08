package com.doctor.eprescription.features.homescreen.template

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.constants.Constants
import com.doctor.eprescription.constants.MedicineItemOption
import com.doctor.eprescription.constants.TemplateTypes
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.AddMyTemplateMedicineRequest
import com.doctor.eprescription.data.remote.model.request.AddPendingMedicineRequest
import com.doctor.eprescription.data.remote.model.request.DeleteMyTemplateMedicineRequest
import com.doctor.eprescription.data.remote.model.request.GetTemplatesRequest
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.doctor.eprescription.data.remote.model.response.common.toMedicine
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
class TemplateViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        const val TAG = "HomeViewModel"
    }

    private val _templateMedicineList = MutableStateFlow(emptyList<MedicineData>())
    val templateMedicineList = _templateMedicineList.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private val patientData =
        savedStateHandle.getStateFlow<PatientsResponse.Patient?>("patient", null)
    val isFromScreen = savedStateHandle.getStateFlow("isFromScreen", 1)
    var isHomeMeds = false

    private val _showDoneButton = MutableStateFlow(false)
    val showDoneButton = _showDoneButton.asStateFlow()

    private val _showAddMoreButton = MutableStateFlow(false)
    val showAddMoreButton = _showAddMoreButton.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    init {
//        val loginResponse = savedStateHandle.getLiveData<LoginResponse>("login_response")
//        Log.e(TAG, loginResponse.data.email)

        viewModelScope.launch {
            val loginData = cacheRepo.getLoginResponse().first()
            val email = loginData?.data?.email

            if (isFromScreen.value == TemplateTypes.HOME_MEDS) {
                isHomeMeds = true
            }

            val title = when (isFromScreen.value) {
                TemplateTypes.MEFTII_TEMPLATE -> "MEFTii Template"
                TemplateTypes.HOME_MEDS -> "Home Meds"
                TemplateTypes.MY_TEMPLATE -> "My Template"
                TemplateTypes.ORGANIZATION_TEMPLATE -> {
                    val org = cacheRepo.getLoginResponse().first()?.data?.organizationName
                    "${org.orEmpty()} Template"
                }

                else -> "Template"
            }
            _title.emit(title)

            val request = when (isFromScreen.value) {
                TemplateTypes.MEFTII_TEMPLATE -> {
                    GetTemplatesRequest(
                        doctorId = -1,
                        patientId = -1,
                        templateType = isFromScreen.value,
                        organizationId = loginData?.data?.organizationId,
                        specialityId = loginData?.data?.specialityId
                    )
                }

                TemplateTypes.HOME_MEDS, TemplateTypes.MY_TEMPLATE, TemplateTypes.ORGANIZATION_TEMPLATE -> {
                    GetTemplatesRequest(
                        doctorId = loginData?.data?.id,
                        patientId = patientData.value?.patientId,
                        templateType = isFromScreen.value,
                        organizationId = loginData?.data?.organizationId,
                        specialityId = loginData?.data?.specialityId
                    )
                }

                else -> {
                    null
                }
            }

            request?.let { getTemplateList(email.orEmpty(), request) }
        }
    }

    fun onPatientClick(patient: PatientsResponse.Patient) {
//        getMenu(patient.toString())
        viewModelScope.launch {
            _channel.send(NavigationEvents.NavigateToPatientMenu(patient))
        }
    }

    private fun addPendingMedicine() {
        viewModelScope.launch {
            showLoader()

            val loginResponse = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.addPendingMedicine(
                AddPendingMedicineRequest(
                    doctorId = loginResponse?.data?.id ?: 0,
                    patientId = patientData.value?.patientId ?: 0,
                    requestType = 1,
                    medicineList = templateMedicineList.value.map { it.toMedicine(true) },
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        showSuccess(ErrorModel("Success", "Medicines added successfully"))
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    private fun getTemplateList(
        email: String, request: GetTemplatesRequest
    ) {
        viewModelScope.launch {
            showLoader()

            when (val call = patientRepo.getTemplateList(
                email = email, request = request
            )) {
                is Resource.Error -> {
                    showError(ErrorModel(title = "Error", message = call.error))
                }

                is Resource.Success -> {
                    hideLoader()
                    _templateMedicineList.emit(call.data.data?.medicinesList ?: emptyList())
                }
            }
        }
    }


    private fun deleteMyTemplateMedicine(medicineId: Int) {
        viewModelScope.launch {
            showLoader()

            when (val call = patientRepo.deleteMyTemplateMedicine(
                DeleteMyTemplateMedicineRequest(
                    myTemplateMedicineId = medicineId
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()/*call.data.data?.let {
                        _pendingMedicine.emit(it.medicinesList)
                    }*/
                }
            }
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
                    deleteMyTemplateMedicine(item.myTemplateMedicineId)
                }
            }
        }
    }

    fun onAddMore() {
        addPendingMedicine()
    }

    fun onMedicineSelected(medicineData: MedicineData) {
        viewModelScope.launch {
            val tempList = _templateMedicineList.value.map {
                if (it.medicineId == medicineData.medicineId) it.toggleSelection()
                else it.copy()
            }
            _templateMedicineList.emit(tempList)
            if (tempList.any { it.isSelected }) {
                _showDoneButton.emit(true)
            } else {
                _showDoneButton.emit(false)
            }
            _showAddMoreButton.emit(_showDoneButton.value.not())
        }
    }

    fun onDoneClick() {
        addMyTemplateMedicine()
    }

    private fun addMyTemplateMedicine() {
        viewModelScope.launch {
            showLoader()
            val loginResponse = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.addMyTemplateMedicine(
                AddMyTemplateMedicineRequest(
                    doctorId = loginResponse?.data?.id.toString(),
                    patientId = patientData.value?.patientId.toString(),
                    myTemplateMedicineIdList = templateMedicineList.value
                        .filter { it.isSelected }
                        .map { it.myTemplateMedicineId }
                ))) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _channel.send(NavigationEvents.NavigateToPrescriptionDetail(patientData.value))
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    sealed class NavigationEvents {
        object NavigateToAddPatient : NavigationEvents()
        data class NavigateToPatientMenu(val patient: PatientsResponse.Patient) : NavigationEvents()
        data class NavigateToPrescribe(
            val patient: PatientsResponse.Patient?, val medicineData: MedicineData
        ) : NavigationEvents()

        data class NavigateToPrescriptionDetail(val patient: PatientsResponse.Patient?) :
            NavigationEvents()
    }
}
