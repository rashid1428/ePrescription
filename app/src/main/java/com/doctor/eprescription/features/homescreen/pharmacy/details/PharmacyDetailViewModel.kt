package com.doctor.eprescription.features.homescreen.pharmacy.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.data.remote.model.response.GetMenuResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PharmacyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val patientData = savedStateHandle.getStateFlow<PatientsResponse.Patient?>("patient", null)
    private val _pharmacyData =
        MutableStateFlow(savedStateHandle.get<Pharmacy?>("pharmacy"))
    val pharmacyData = _pharmacyData.asStateFlow()

    private val _expandPatientDetails = MutableStateFlow(false)
    val expandPatientDetails = _expandPatientDetails.asStateFlow()

    init {

    }

    fun onExpandPatientDetailClick() {
        viewModelScope.launch {
            _expandPatientDetails.emit(_expandPatientDetails.value.not())
        }
    }

    fun onPharmacyChanged(pharmacy: Pharmacy?) {
        viewModelScope.launch {
            _pharmacyData.emit(pharmacy)
        }
    }
}