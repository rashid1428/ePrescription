package com.doctor.eprescription.features.homescreen.allergies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.data.AllergiesData
import com.doctor.eprescription.data.remote.model.request.UpdateFoodAndDrugAllergiesRequest
import com.doctor.eprescription.data.remote.model.response.FoodAllergy
import com.doctor.eprescription.data.remote.model.response.FoodAndAllergiesListResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllergiesDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val patientRepo: PatientRepo
) : BaseViewModel() {

    val patientData = savedStateHandle.getStateFlow<PatientsResponse.Patient?>("patient", null)
    val allergiesData = savedStateHandle.get<AllergiesData>("allergies")

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private var foodAllergies: List<FoodAndAllergiesListResponse.FoodAllergy> = arrayListOf()

    private val _expandPatientDetails = MutableStateFlow(false)
    val expandPatientDetails = _expandPatientDetails.asStateFlow()

    private val _allergies = MutableStateFlow("")
    val allergies = _allergies.asStateFlow()

    init {
        getFoodAllergies()
    }

    fun onExpandPatientDetailClick() {
        viewModelScope.launch {
            _expandPatientDetails.emit(_expandPatientDetails.value.not())
        }
    }

    fun getFoodAllergies() {
        var foodAllergy = ""
        viewModelScope.launch {
            allergiesData?.foodAllergies?.let { foodAllergies ->
                for (data in foodAllergies) {
                    foodAllergy = if (foodAllergy.isEmpty()) data.name
                    else foodAllergy.plus(", ").plus(data.name)
                }
            }

            allergiesData?.drugAllergies?.let { drugAllergies ->
                for (name in drugAllergies) {
                    foodAllergy = if (foodAllergy.isEmpty()) name
                    else foodAllergy.plus(", ").plus(name)
                }
            }
            if (foodAllergy.isEmpty()) foodAllergy = "No Allergy Found"
            _allergies.emit(foodAllergy)
        }
    }

    fun onFoodAllergiesClick() {
        viewModelScope.launch {
            showLoader()
            when (val call = patientRepo.getFoodAndAllergiesList()) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        foodAllergies = call.data.data.foodAllergies

                        val selectedItems: ArrayList<SelectionModel> = arrayListOf()
                        if (_allergies.value.isNotEmpty()) {
                            _allergies.value.split(",").forEach { allergy ->
                                call.data.data.foodAllergies.find {
                                    it.name.equals(
                                        allergy.trim(),
                                        true
                                    )
                                }?.let {
                                    selectedItems.add(
                                        SelectionModel(
                                            id = it.id.toString(),
                                            text = it.name
                                        )
                                    )
                                }

                            }
                        }

                        _channel.send(
                            NavigationEvents.ShowFoodAllergiesDialog(
                                list = call.data.data.foodAllergies.map {
                                    SelectionModel(
                                        id = it.id.toString(),
                                        text = it.name
                                    )
                                },
                                selectedItems = selectedItems
                            )
                        )
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    fun onFoodAndAllergiesUpdate(allergies: List<SelectionModel>) {
        viewModelScope.launch {
            val selectedIds = allergies.map { it.id }
            when (val call = patientRepo.updatePatientFoodAndDrugAllergies(
                request = UpdateFoodAndDrugAllergiesRequest(
                    patientId = patientData.value?.patientId ?: 0,
                    foodAllergies = foodAllergies
                        .filter { selectedIds.contains(it.id.toString()) }
                        .map {
                            UpdateFoodAndDrugAllergiesRequest.FoodAllergy(
                                id = it.id,
                                isOtherFoodAllergy = it.isOther,
                                otherFoodAllergy = it.otherFoodAllergy,
                                name = it.name
                            )
                        },
                    drugAllergies = emptyList()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {

                        var foodAllergy = ""
                        for (allergy in allergies) {
                            foodAllergy = if (foodAllergy.isEmpty()) allergy.text
                            else foodAllergy.plus(", ").plus(allergy.text)
                        }
                        if (foodAllergy.isEmpty()) foodAllergy = "No Allergy Found"
                        _allergies.emit(foodAllergy)

                        showSuccess(
                            ErrorModel(
                                "Success",
                                "Food and Drug Allergies updated successfully."
                            )
                        )

                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    sealed class NavigationEvents {
        data class ShowFoodAllergiesDialog(
            val list: List<SelectionModel>,
            val selectedItems: List<SelectionModel> = emptyList()
        ) : NavigationEvents()
    }
}