package com.doctor.eprescription.features.homescreen.pharmacylist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.databinding.FragmentPharmacyListBinding
import com.doctor.eprescription.extension.collectWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PharmacyListFragment :
    BaseFragmentVB<FragmentPharmacyListBinding>(FragmentPharmacyListBinding::inflate) {

    private val viewModel by viewModels<PharmacyListViewModel>()

    private val mAdapter by lazy {
        PharmacyListAdapter {
            viewModel.onPharmacySelected(it)
        }
    }
    private val tabList = ArrayList<Pair<AppCompatButton, Int>>()

    override fun getMyViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setUpClickListeners()
        setObservers()
    }

    private fun setObservers() {
        collectWhenStarted {
            viewModel.pharmacyList.collectLatest {
                Log.e("PharmacyFragment", it.size.toString())
                mAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.showDoneButton.collectLatest { binding.doneButton.isVisible = it }
        }

        collectWhenStarted {
            viewModel.selectedTabPosition.collectLatest { position ->
                tabList.forEach {
                    it.first.isSelected = it.second == position
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is PharmacyListViewModel.NavigationEvents.OnPharmacyUpdated -> {
                        setFragmentResult("pharmacy_updated", Bundle().apply {
                            putParcelable("pharmacy", event.pharmacy)
                        })
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setUpClickListeners() {
        binding.apply {
            compBackButton.root.setOnClickListener { findNavController().popBackStack() }

            tabExpanded.setOnClickListener {
                viewModel.onTabSelected(0)
            }
            tabRetail.setOnClickListener {
                viewModel.onTabSelected(1)
            }
            tabMailOrder.setOnClickListener {
                viewModel.onTabSelected(2)
            }
        }
    }

    private fun setupUi() {
        binding.apply {
            compSearch.etSearch.hint = "Search by Name, City, State & ZIP code"

            tabList.add(tabExpanded to 0)
            tabList.add(tabRetail to 1)
            tabList.add(tabMailOrder to 2)

            rvPharmacy.layoutManager = LinearLayoutManager(requireContext())
            rvPharmacy.itemAnimator = null
            rvPharmacy.adapter = mAdapter

            doneButton.setOnClickListener {
                viewModel.onDoneClick()
            }

            compSearch.etSearch.doOnTextChanged { text, start, before, count ->
                viewModel.onSearch(
                    text.toString()
                )
            }
        }
    }
}