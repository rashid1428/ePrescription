package com.doctor.eprescription.features.homescreen.prescription.medicines

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.GetMedicineRequest
import com.doctor.eprescription.data.remote.model.response.GetMedicineResponse
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineDialogViewModel @Inject constructor(
    private val patientRepo: PatientRepo
) : BaseViewModel() {

    private var fullList: List<SelectionModel> = emptyList()
    private var query = ""
    var selectedItem: SelectionModel? = null

    private var _list = MutableStateFlow<List<SelectionModel>>(emptyList())
    val list = _list.asStateFlow()

    private var medicineList: List<GetMedicineResponse.Medicine> = emptyList()

    private val _heading = MutableStateFlow("")
    val heading = _heading.asStateFlow()

    private val _showDoneButton = MutableStateFlow(false)
    val showDoneButton = _showDoneButton.asStateFlow()


    init {
        viewModelScope.launch {
            _heading.emit("Medicines")
            callSearchApi()
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        callSearchApi()
    }

    fun search(query: String) {
        this@MedicineDialogViewModel.query = query
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 500)
    }

    private fun callSearchApi() {
        viewModelScope.launch {
            showLoader()

            when (val call = patientRepo.getMedicine(
                request = GetMedicineRequest(
                    searchString = query,
                    dosage = "",
                    strength = "",
                    startRow = "",
                    pageSize = "10"
                )
            )) {
                is Resource.Error -> showError(ErrorModel(title = "Error", message = call.error))
                is Resource.Success -> {
                    hideLoader()
                   call.data.data?.medicines?.let {
                       medicineList = it
                   }
                    val medicines = mutableListOf<SelectionModel>().apply {
                        call.data.data?.medicines?.forEach {
                            add(SelectionModel(it.id, it.name, false))
                        }
                    }
                    fullList = medicines
                    _list.emit(medicines)
                }
            }
        }
    }

    fun getSelectedItem(): GetMedicineResponse.Medicine {
        return medicineList.find { it.id == selectedItem?.id }!!
    }

     fun onItemSelected(model: SelectionModel) {
        viewModelScope.launch {
            _showDoneButton.emit(true)
            selectedItem = model
            _list.emit(
                fullList.map {
                    if (it == model) it.copy(isSelected = true) else it.copy(isSelected = false)
                }
            )
        }
    }
}