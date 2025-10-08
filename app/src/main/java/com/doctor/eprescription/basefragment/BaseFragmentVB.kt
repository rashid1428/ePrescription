package com.doctor.eprescription.basefragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.extension.*
import kotlinx.coroutines.flow.collectLatest


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragmentVB<VB : ViewBinding>(
    private val inflate: Inflate<VB>,
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    private lateinit var progressDialog: AlertDialog
    private var errorDialog: AlertDialog? = null
    private var successDialog: AlertDialog? = null
    private lateinit var prevState: ErrorLoadingState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater, container, false)

        progressDialog = progressDialog()
        observeLoader()

        return binding.root
    }

    private fun observeLoader() {
        collectWhenStarted {
            getMyViewModel().loader.collect { state ->
                //The state flow was emitting same value twice
                if (this::prevState.isInitialized && state == prevState) return@collect

                prevState = state
//                Log.e("BaseFragmentVB", "state = $state")
                when (state) {
                    is ErrorLoadingState.Error -> {
                        progressDialog.visibility(false)
                        successDialog.safeDismiss()

                        errorDialog = errorDialog(state.errorModel) {
                            getMyViewModel().hideError()
                        }
                        errorDialog.safeShow()
                    }

                    ErrorLoadingState.Idle -> {
                        progressDialog.visibility(false)

                        errorDialog.safeDismiss()
                        successDialog.safeDismiss()
                    }

                    ErrorLoadingState.Loading -> {
                        progressDialog.visibility(true)

                        errorDialog.safeDismiss()
                        successDialog.safeDismiss()
                    }

                    is ErrorLoadingState.Success -> {
                        errorDialog.safeDismiss()
                        progressDialog.visibility(false)

                        successDialog = successDialog(state.errorModel) {
                            getMyViewModel().hideError()
                        }
                        successDialog.safeShow()
                    }
                }
            }
        }
    }

    abstract fun getMyViewModel(): BaseViewModel

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}