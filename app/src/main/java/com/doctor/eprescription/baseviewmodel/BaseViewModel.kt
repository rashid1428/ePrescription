package com.doctor.eprescription.baseviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.basefragment.ErrorLoadingState
import com.doctor.eprescription.domain.model.ErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _loader = MutableStateFlow<ErrorLoadingState>(ErrorLoadingState.Idle)
    val loader = _loader.asStateFlow()

    fun showLoader() {
        viewModelScope.launch { _loader.emit(ErrorLoadingState.Loading) }
    }

    fun hideLoader() {
        viewModelScope.launch { _loader.emit(ErrorLoadingState.Idle) }
    }

    fun showError(errorModel: ErrorModel) {
        viewModelScope.launch { _loader.emit(ErrorLoadingState.Error(errorModel)) }
    }

    fun showSuccess(errorModel: ErrorModel) {
        viewModelScope.launch { _loader.emit(ErrorLoadingState.Success(errorModel)) }
    }

    fun hideError() {
        viewModelScope.launch { _loader.emit(ErrorLoadingState.Idle) }
    }
}