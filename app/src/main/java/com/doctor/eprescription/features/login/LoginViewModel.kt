package com.doctor.eprescription.features.login

import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.LoginRequest
import com.doctor.eprescription.data.remote.model.response.LoginResponse
import com.doctor.eprescription.domain.interactors.EmailValidationUseCase
import com.doctor.eprescription.domain.interactors.PasswordValidationUseCase
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.LoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo,
    private val cacheRepo: CacheRepo,
    private val emailValidationUseCase: EmailValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase
) : BaseViewModel() {

    private val _emailErrorFlow = MutableStateFlow("")
    val emailErrorFlow = _emailErrorFlow.asStateFlow()

    private val _passwordErrorFlow = MutableStateFlow("")
    val passwordErrorFlow = _passwordErrorFlow.asStateFlow()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    fun onLoginClicked(email: String, password: String) {
        viewModelScope.launch {
            if (emailValidationUseCase.isValid(email)) {
                _emailErrorFlow.emit("")
            } else {
                _emailErrorFlow.emit("Valid Email is required")
                return@launch
            }

            if (passwordValidationUseCase.isValid(password)) {
                _passwordErrorFlow.emit("")
            } else {
                _passwordErrorFlow.emit("Password is required")
                return@launch
            }

            showLoader()
            when (val call = loginRepo.login(
                email = email,
                loginRequest = LoginRequest(password)
            )) {
                is Resource.Error -> {
                    hideLoader()
                    showError(ErrorModel(title = "Error", message = call.error))
                }
                is Resource.Success -> {
                    hideLoader()
                    cacheRepo.setLoggedIn(true)
                    cacheRepo.saveLoginResponse(call.data)
                    _channel.send(NavigationEvents.NavigateToMainScreen(call.data))
                }
            }
        }
    }

    fun onEmailTextChanged(text: String) {
        viewModelScope.launch {
            if (emailValidationUseCase.isValid(text)) {
                _emailErrorFlow.emit("")
            }
        }
    }

    fun onPasswordChanged(text: String) {
        viewModelScope.launch {
            if (passwordValidationUseCase.isValid(text)) {
                _passwordErrorFlow.emit("")
            }
        }
    }

    sealed class NavigationEvents {
        data class NavigateToMainScreen(val loginResponse: LoginResponse) : NavigationEvents()
    }
}