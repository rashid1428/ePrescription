package com.doctor.eprescription.features.homescreen.display

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.DeletePatientRequest
import com.doctor.eprescription.data.remote.model.request.GetMenuRequest
import com.doctor.eprescription.data.remote.model.request.GetPatientRequest
import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import com.doctor.eprescription.entrypoint.model.DrawerItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    private val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        const val TAG = "HomeViewModel"
    }

    private val allPatients = ArrayList<PatientsResponse.Patient>()
    private val _patients = MutableStateFlow(emptyList<PatientsResponse.Patient>())
    val patients = _patients.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private val _drawerItems = MutableStateFlow(emptyList<DrawerItemModel>())
    val drawerItems = _drawerItems.asStateFlow()

    private val _showDrawer = MutableStateFlow(false)
    val showDrawer = _showDrawer.asStateFlow()

    val loginResponse = savedStateHandle.getStateFlow<LoginResponse?>("login_response", null)

    init {
//        val loginResponse = savedStateHandle.getLiveData<LoginResponse>("login_response")
//        Log.e(TAG, loginResponse.data.email)

        viewModelScope.launch {
            _drawerItems.emit(
                listOf(
                    DrawerItemModel("Profile"),
                    DrawerItemModel("Contact Us"),
                    DrawerItemModel("Logout"),
//                    DrawerItemModel("On Call Group"),
//                    DrawerItemModel("ePrescription"),
//                    DrawerItemModel("SOAP Note"),
//                    DrawerItemModel("Medical Records"),
//                    DrawerItemModel("E-Visit Free"),
//                    DrawerItemModel("Invite People"),
                )
            )
        }

        viewModelScope.launch {
            getPatients()
        }
    }

    fun onPatientClick(patient: PatientsResponse.Patient) {
//        getMenu(patient.toString())
        viewModelScope.launch {
            _channel.send(NavigationEvents.NavigateToPatientMenu(patient, loginResponse.value))
        }
    }

    private fun getPatients() {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.getPatients(
                email = loginResponse.value?.data?.email.orEmpty(),
                request = GetPatientRequest(doctorId = loginResponse.value?.data?.id.toString())
            )) {
                is Resource.Error -> {
                    showError(ErrorModel(title = "Error", message = call.error))
                }
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        call.data.data?.patients?.let {
                            allPatients.clear()
                            allPatients.addAll(it)
                            _patients.emit(call.data.data.patients)
                        }
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    fun openDrawer() {
        viewModelScope.launch { _showDrawer.emit(true) }
    }

    fun closeDrawer() {
        viewModelScope.launch { _showDrawer.emit(false) }
    }

    private fun getMenu(patientId: Int) {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.getMenu(
                GetMenuRequest(patientId = patientId)
            )) {
                is Resource.Error -> showError(ErrorModel(title = "Error", message = call.error))
                is Resource.Success -> {
                    hideLoader()
                }
            }
        }
    }

    fun onAddPatientClick() {
        viewModelScope.launch { _channel.send(NavigationEvents.NavigateToAddPatient(null)) }
    }

    fun onEditPatientClick(patient: PatientsResponse.Patient) {
        viewModelScope.launch { _channel.send(NavigationEvents.NavigateToAddPatient(patient)) }
    }

    fun onDeletePatientClick(patient: PatientsResponse.Patient) {
        viewModelScope.launch {
            showLoader()

            val loginData = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.deletePatient(
                DeletePatientRequest(
                    doctorId = loginData?.data?.id ?: 0,
                    patientId = patient.patientId
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        //Show success dialog
                        showSuccess(ErrorModel("Success", "Patient deleted successfully."))
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    fun refreshPatientList() {
        getPatients()
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            _patients.emit(allPatients.filter { it.search(query) })
        }
    }

    sealed class NavigationEvents {
        data class NavigateToAddPatient(
            val patient: PatientsResponse.Patient?
        ) : NavigationEvents()

        data class NavigateToPatientMenu(
            val patient: PatientsResponse.Patient,
            val loginResponse: LoginResponse?
        ) : NavigationEvents()
    }
}
