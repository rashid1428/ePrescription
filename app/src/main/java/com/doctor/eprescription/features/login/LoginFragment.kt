package com.doctor.eprescription.features.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.databinding.FragmentLoginBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.helper.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragmentVB<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener {
                viewModel.onLoginClicked(
                    tieEmail.text.toString(),
                    tiePassword.text.toString()
                )
            }

            tieEmail.doOnTextChanged { text, start, before, count ->
                viewModel.onEmailTextChanged(text.toString())
            }

            tiePassword.doOnTextChanged { text, start, before, count ->
                viewModel.onPasswordChanged(text.toString())
            }
        }

        addObservers()
    }

    override fun getMyViewModel() = viewModel

    private fun addObservers() {
        collectWhenStarted {
            viewModel.emailErrorFlow.collectLatest {
                binding.tilEmail.error = it
            }
        }

        collectWhenStarted {
            viewModel.passwordErrorFlow.collectLatest {
                binding.tilPassword.error = it
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is LoginViewModel.NavigationEvents.NavigateToMainScreen -> {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment(
                                event.loginResponse
                            )
                        )
                    }
                }
            }
        }
    }

}