package com.doctor.eprescription.features.homescreen.erxstatus

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.databinding.FragmentErxStatusBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ErxStatusFragment :
    BaseFragmentVB<FragmentErxStatusBinding>(FragmentErxStatusBinding::inflate) {

    private val viewModel by viewModels<ErxStatusViewModel>()
    private val mAdapter by lazy {
        ErxStatusAdapter {
            viewModel.onIntent(ErxStatusIntent.OnItemClick(it))
        }
    }

    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpClickListeners()
        initAdapter()
        addObservers()
    }

    private fun initAdapter() {
        binding.rvErx.layoutManager = LinearLayoutManager(requireContext())
        binding.rvErx.adapter = mAdapter
    }

    private fun setUpClickListeners() {
        binding.apply {
            compBackButton.root.setOnClickListener { findNavController().popBackStack() }
            multiToggleButton.setOnValueChangedListener {
                viewModel.onIntent(ErxStatusIntent.OnTabChanged(it))
            }
        }
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is ErxStatusViewModel.NavigationEvents.NavigateToErxHistory -> {
                        val action =
                            ErxStatusFragmentDirections.actionErxHistoryFragmentToErxHistoryFragment2(
                                event.patient, event.pharmacy, event.medicine
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.uiState.collectLatest { state ->
                binding.multiToggleButton.setElements(
                    state.tabs,
                    state.tabsSelection.toBooleanArray()
                )

                mAdapter.submitList(state.erxStatusList)
            }
        }
    }
}