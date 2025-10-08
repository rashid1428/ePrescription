package com.doctor.eprescription.features.homescreen.patientmenu

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.R
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.constants.ArgsConstant
import com.doctor.eprescription.constants.MenuOptions
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.data.AllergiesData
import com.doctor.eprescription.data.remote.model.request.GetMenuRequest
import com.doctor.eprescription.data.remote.model.response.GetMenuResponse
import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.model.PatientMenuItem
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientMenuViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    private val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _menuItems = MutableStateFlow<List<PatientMenuItem>>(emptyList())
    val menuItem = _menuItems.asStateFlow()

    private val _patientData = MutableStateFlow<PatientsResponse.Patient?>(null)
    var patientData: StateFlow<PatientsResponse.Patient?> =
        MutableStateFlow<PatientsResponse.Patient?>(null)

    private var getMenuResponse: GetMenuResponse? = null

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    val patient = savedStateHandle.get<PatientsResponse.Patient>(ArgsConstant.PATIENT)

    private val loginResponse = savedStateHandle.get<LoginResponse>("login_response")

    init {
        patientData = savedStateHandle.getStateFlow(ArgsConstant.PATIENT, null)

        getPatientMenuItems()
//        getMenu()
        Log.e("PatientMenuViewModel", "Patient name = ${patient?.firstName}")
    }

    private fun getPatientMenuItems() {
        viewModelScope.launch {
            _menuItems.emit(
                listOf(
                    PatientMenuItem(
                        R.drawable.menu_eprescription,
                        "Prescribe Meds",
                        MenuOptions.PRESCRIBE_MEDS
                    ),
                    PatientMenuItem(
                        R.drawable.menu_erx_status,
                        "eRx Status",
                        MenuOptions.ERX_STATUS
                    ),
                    PatientMenuItem(
                        R.drawable.menu_my_template,
                        "${loginResponse?.data?.organizationName} Template",
                        MenuOptions.ORGANIZATION_TEMPLATE
                    ),
                    PatientMenuItem(
                        R.drawable.menu_meftii_template,
                        "MEFTii Template",
                        MenuOptions.MEFTII_TEMPLATE
                    ),
                    PatientMenuItem(
                        R.drawable.menu_my_template,
                        "My Template",
                        MenuOptions.MY_TEMPLATE
                    ),
                    PatientMenuItem(R.drawable.menu_home_meds, "Home Meds", MenuOptions.HOME_MEDS),
                    PatientMenuItem(
                        R.drawable.menu_food_and_drug,
                        "Food and Drug Allergies",
                        MenuOptions.FOOD_AND_DRUG_ALLERGIES
                    ),
                    PatientMenuItem(R.drawable.menu_pharmacy, "Pharmacy", MenuOptions.PHARMACY),
                )
            )
        }
    }

    fun onMenuItemClick(item: PatientMenuItem) {
        viewModelScope.launch {
            when (item.id) {
                MenuOptions.PRESCRIBE_MEDS -> {
                    _channel.send(
                        NavigationEvents.NavigateToPrescription(
                            patientData.value!!,
                            getMenuResponse?.data?.pharmacy
                        )
                    )
                }

                MenuOptions.MY_TEMPLATE -> {
                    _channel.send(NavigationEvents.NavigateToTemplate(patientData.value))
                }

                MenuOptions.MEFTII_TEMPLATE -> {
                    _channel.send(NavigationEvents.NavigateToMeftiiTemplate(patientData.value))
                }

                MenuOptions.HOME_MEDS -> {
                    _channel.send(NavigationEvents.NavigateToHomeMeds(patientData.value))
                }

                MenuOptions.ERX_STATUS -> {
                    _channel.send(
                        NavigationEvents.NavigateToErxStatus(
                            patientData.value,
                            getMenuResponse?.data?.pharmacy
                        )
                    )
                }

                MenuOptions.PHARMACY -> {
                    if (getMenuResponse?.data?.pharmacy != null) {
                        _channel.send(NavigationEvents.NavigateToPharmacy(getMenuResponse?.data?.pharmacy, patient))
                    }
                }

                MenuOptions.FOOD_AND_DRUG_ALLERGIES -> {
                    _channel.send(
                        NavigationEvents.NavigateToAllergies(
                            AllergiesData(
                                getMenuResponse?.data?.foodAllergies,
                                getMenuResponse?.data?.drugAllergies
                            ),
                            patient
                        )
                    )
                }
                MenuOptions.ORGANIZATION_TEMPLATE -> {
                    _channel.send(NavigationEvents.NavigateToOrganizationTemplate(patientData.value))
                }
            }
        }
    }

    fun getMenu() {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.getMenu(
//                email = loginResponse?.data?.email.orEmpty(),
                GetMenuRequest(patientId = patient?.patientId ?: 0)
            )) {
                is Resource.Error -> showError(ErrorModel(title = "Error", message = call.error))
                is Resource.Success -> {
                    hideLoader()
                    getMenuResponse = call.data
                }
            }
        }
    }

    sealed class NavigationEvents {
        data class NavigateToPrescription(
            val patient: PatientsResponse.Patient,
            val pharmacy: Pharmacy?
        ) : NavigationEvents()

        data class NavigateToPharmacy(val pharmacy: Pharmacy?, val patient: PatientsResponse.Patient?) : NavigationEvents()
        data class NavigateToAllergies(val allergiesData: AllergiesData, val patient: PatientsResponse.Patient?) : NavigationEvents()
        data class NavigateToTemplate(val patient: PatientsResponse.Patient?) : NavigationEvents()
        data class NavigateToMeftiiTemplate(val patient: PatientsResponse.Patient?) :
            NavigationEvents()

        data class NavigateToOrganizationTemplate(val patient: PatientsResponse.Patient?) :
            NavigationEvents()

        data class NavigateToHomeMeds(val patient: PatientsResponse.Patient?) : NavigationEvents()
        data class NavigateToErxStatus(
            val patient: PatientsResponse.Patient?,
            val pharmacy: Pharmacy?
        ) : NavigationEvents()
    }
}