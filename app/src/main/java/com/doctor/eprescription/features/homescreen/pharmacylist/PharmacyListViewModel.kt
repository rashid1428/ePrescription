package com.doctor.eprescription.features.homescreen.pharmacylist

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.constants.ArgsConstant
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.GetPharmacyRequest
import com.doctor.eprescription.data.remote.model.request.UpdatePharmacyRequest
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.PatientRepo
import com.doctor.eprescription.features.homescreen.prescription.details.PrescriptionDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PharmacyListViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val selectedPharmacy = savedStateHandle.get<Pharmacy?>("pharmacy")
    val patient = savedStateHandle.get<PatientsResponse.Patient>(ArgsConstant.PATIENT)

    private val allPharmacies = ArrayList<Pharmacy>()
    private val _pharmacyList = MutableStateFlow<List<Pharmacy>>(emptyList())
    val pharmacyList = _pharmacyList.asStateFlow()

    private val _selectedTabPosition = MutableStateFlow(0)
    val selectedTabPosition = _selectedTabPosition.asStateFlow()

    private val _showDoneButton = MutableStateFlow(false)
    val showDoneButton = _showDoneButton.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    init {
        getPharmacyList()
    }

    private fun getPharmacyList() {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.getPharmacy(GetPharmacyRequest())) {
                is Resource.Error -> showError(ErrorModel(title = "Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        allPharmacies.clear()
                        allPharmacies.addAll(call.data.data.pharmacyData)

                        if (selectedPharmacy != null) {
                            doPharmacySelection(selectedPharmacy)
                        } else {
                            _pharmacyList.emit(allPharmacies.map { it.copy(_isSelected = false) })
                        }

                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    private suspend fun doPharmacySelection(selectedPharmacy: Pharmacy) {
        val tempList = allPharmacies.map {
            if (it.id == selectedPharmacy.id) {
                it.copy(_isSelected = true)
            } else it.copy(_isSelected = false)
        }
        allPharmacies.clear()
        allPharmacies.addAll(tempList)
        filterPharmacies()
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            _pharmacyList.emit(allPharmacies.filter { it.search(query) })
        }
    }

    fun onTabSelected(tabPosition: Int) {
        viewModelScope.launch {
            _selectedTabPosition.emit(tabPosition)

            filterPharmacies(tabPosition)
        }
    }

    private suspend fun filterPharmacies(tabPosition: Int = selectedTabPosition.value) {
        val tempList = allPharmacies.filter {
            when (tabPosition) {
                0 -> true
                1 -> it.retail
                2 -> it.mailOrder
                else -> true

            }
        }
        _pharmacyList.emit(tempList)

        Log.e("PharmacyListViewModel", pharmacyList.value.size.toString())
        _showDoneButton.emit(tempList.any { it.isSelected })
    }

    fun onPharmacySelected(pharmacy: Pharmacy) {
        viewModelScope.launch {
            doPharmacySelection(pharmacy)
        }
    }

    private fun onPharmacyChanged(selectedPharmacy: Pharmacy?) {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.updatePharmacy(
                UpdatePharmacyRequest(
                    patientId = patient?.patientId.toString(),
                    pharmacyId = selectedPharmacy?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _channel.send(NavigationEvents.OnPharmacyUpdated(selectedPharmacy))
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    fun onDoneClick() {
        onPharmacyChanged(allPharmacies.find { it.isSelected })
    }

    sealed class NavigationEvents {
        data class OnPharmacyUpdated(val pharmacy: Pharmacy?) : NavigationEvents()
    }

}