package com.doctor.eprescription.features.homescreen.prescription.medicines

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.FullScreenDialogFragment
import com.doctor.eprescription.components.listselection.SelectionAdapter
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.model.response.GetMedicineResponse
import com.doctor.eprescription.databinding.DialogSearchSelectBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
open class MedicinesDialog :
    FullScreenDialogFragment<DialogSearchSelectBinding>(DialogSearchSelectBinding::inflate) {

    private var onDoneClick: ((item: GetMedicineResponse.Medicine) -> Unit)? = null

    companion object {

        fun show(fragmentManager: FragmentManager, onItemSelected: (item: GetMedicineResponse.Medicine) -> Unit) {

            val dialog = MedicinesDialog()
            dialog.onDoneClick = onItemSelected
            dialog.show(fragmentManager, "abcd")
        }
    }

    private val viewModel by viewModels<MedicineDialogViewModel>()
    override fun getMyViewModel() = viewModel
    private val mAdapter by lazy { SelectionAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvCommonInfoList.layoutManager = LinearLayoutManager(requireContext())
            rvCommonInfoList.adapter = mAdapter

            mAdapter.setSelectionCallback {
                viewModel.onItemSelected(it)
            }

            etSearch.doOnTextChanged { text, start, before, count ->
                viewModel.search(text.toString())
            }

            doneButton.setOnClickListener {
                onDoneClick?.invoke(viewModel.getSelectedItem())
                dismiss()
            }

            backer.root.setOnClickListener { dismiss() }
        }

        addObservers()
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.heading.collectLatest {
                binding.tvHeading.text = it
            }
        }

        collectWhenStarted {
            viewModel.showDoneButton.collectLatest {
                binding.doneButton.isVisible = it
            }
        }

        collectWhenStarted {
            viewModel.heading.collectLatest {
                binding.tvHeading.text = it
            }
        }

        collectWhenStarted {
            viewModel.list.collectLatest {
                mAdapter.submitList(it)
            }
        }
    }
}