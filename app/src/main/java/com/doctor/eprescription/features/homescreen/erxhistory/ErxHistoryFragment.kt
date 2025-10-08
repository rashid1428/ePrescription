package com.doctor.eprescription.features.homescreen.erxhistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.databinding.FragmentErxHistoryBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.features.homescreen.prescription.detaildialog.PrescriptionDetailDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ErxHistoryFragment :
    BaseFragmentVB<FragmentErxHistoryBinding>(FragmentErxHistoryBinding::inflate) {

    private val viewModel by viewModels<ErxHistoryViewModel>()
    private val mAdapter by lazy {
        ErxHistoryAdapter {
            viewModel.onIntent(ErxHistoryIntent.OnCancelClick(it))
        }
    }

    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()
        setClickListeners()
        addObservers()
    }

    private fun setClickListeners() {
        binding.apply {
            compBackButton.root.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpAdapter() {
        binding.rvErx.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.uiState.collectLatest { state ->
                mAdapter.submitList(state.erxStatusList)
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is ErxHistoryViewModel.NavigationEvents.NavigateToCancelPrescriptionDetailDialog -> {
                        PrescriptionDetailDialogFragment.show(
                            requireActivity().supportFragmentManager,
                            title = event.title,
                            patient = event.patient,
                            pharmacy = event.pharmacy,
                            medicine = event.medicine,
                        )
                    }
                }
            }
        }
    }


}