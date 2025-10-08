package com.doctor.eprescription.features.homescreen.prescription.createprescription

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.components.listselection.ListSelectionDialog
import com.doctor.eprescription.databinding.FragmentCreatePrescriptionBinding
import com.doctor.eprescription.dialog.ConfirmationDialogFragment
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.extension.errorDialog
import com.doctor.eprescription.extension.gone
import com.doctor.eprescription.extension.setTextNotEmpty
import com.doctor.eprescription.extension.visible
import com.doctor.eprescription.features.homescreen.prescription.medicines.MedicinesDialog
import com.doctor.eprescription.utils.DecimalInputFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CreatePrescriptionFragment :
    BaseFragmentVB<FragmentCreatePrescriptionBinding>(FragmentCreatePrescriptionBinding::inflate) {

    private lateinit var infoDialog: AlertDialog
    private val viewModel by viewModels<CreatePrescriptionViewModel>()

    private val mCompoundingAdapter by lazy {
        CompoundingAdapter(
            onAddClick = {
                onEvent(CreatePrescriptionIntent.OnAddCompoundingItem)
            },
            onRemoveClick = {
                onEvent(CreatePrescriptionIntent.OnRemoveCompoundingItem(it))
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()

        addObservers()
    }

    private fun onEvent(intent: CreatePrescriptionIntent) {
        viewModel.onIntent(intent)
    }

    private fun setUpUi() {
        binding.apply {
//            fetMedicines.bindInputWithSwitch(swMedicine)
            fetDispenseQuantity.getEditText().filters = arrayOf(DecimalInputFilter())
            compBackButton.root.setOnClickListener { findNavController().popBackStack() }

            rvCompounding.layoutManager = LinearLayoutManager(requireContext())
            rvCompounding.adapter = mCompoundingAdapter

            swFreeText.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(CreatePrescriptionIntent.OnFreeTextMedicineSwitch(isChecked))
            }
            swMaySubstitute.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(CreatePrescriptionIntent.OnMaySubstituteCheck(isChecked))
            }
            swSig.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(
                    CreatePrescriptionIntent.OnSigSwitch(isChecked)
                )
            }

            swAddToMyTemplate.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(CreatePrescriptionIntent.OnAddToMyTemplateCheck(isChecked))
            }

            cbCompounding.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(CreatePrescriptionIntent.OnCompoundingCheck(isChecked))
            }

            cbMedicalSupply.setOnCheckedChangeListener { buttonView, isChecked ->
                onEvent(CreatePrescriptionIntent.OnMedicalSupplyCheck(isChecked))
            }

            fetMedicines.onClick {
                MedicinesDialog.show(childFragmentManager) {
                    onEvent(CreatePrescriptionIntent.OnMedicineSelected(it))
                }
            }

            fetDoseUnit.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnDoseUnitClick)
            }

            fetRoute.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnRouteClick)
            }

            fetFrequency.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnFrequencyClick)
            }

            fetDispenseQuantityUnit.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnQuantityClick)
            }

            fetDiagnosis.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnDiagnosisClick)
            }

            fetRefill.actAsClickItem {
                onEvent(CreatePrescriptionIntent.OnRefillClick)
            }

            tvSkip.setOnClickListener {
                onEvent(CreatePrescriptionIntent.OnSkip)
            }

            fetDose.getEditText().doOnTextChanged { text, start, before, count ->
                onEvent(CreatePrescriptionIntent.OnDoseTextChanged(text.toString()))
            }

            btnAddMore.setOnClickListener {
                onEvent(
                    CreatePrescriptionIntent.OnNextClick(
                        dynamicSig = fetDynamicSig.getText(),
                        doseText = fetDose.getText(),
                        specialInstructions = fetSpecialInstructions.getText(),
                        dispenseQuantityText = fetDispenseQuantity.getText(),
                        freeText = if (binding.swFreeText.isChecked) binding.fetMedicines.getText() else ""
                    )
                )
            }
        }
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    swFreeText.isChecked = state.swFreeTextMedicine
                    fetMedicines.bindInputWithSwitch(state.swFreeTextMedicine, false)
                    cbCompounding.isChecked = state.cbCompounding
                    cbMedicalSupply.isChecked = state.cbMedicalSupply

                    tvTitle.text = state.title

                    fetMedicines.isVisible = cbCompounding.isChecked.not()
                    llCompounding.isVisible = cbCompounding.isChecked
                    mCompoundingAdapter.submitList(state.compoundingList)

                    fetSpecialInstructions.setTextNotEmpty(state.specialInstructions)
                    fetDose.setTextNotEmpty(state.doseText)
                    fetDispenseQuantity.setTextNotEmpty(state.dispenseQuantityText)

                    state.selectedMedicine?.let { fetMedicines.setText(it.name) }
                    state.selectedDoseUnit?.let { fetDoseUnit.setText(it.name) }
                    state.selectedRoute?.let { fetRoute.setText(it.name) }
                    state.selectedDirection?.let { fetFrequency.setText(it.name) }
                    state.selectedQuantity?.let { fetDispenseQuantityUnit.setText(it.name) }
                    state.selectedIcdCode?.let { fetDiagnosis.setText(it.getFullText) }
                    state.selectedRefill?.let { fetRefill.setText(it.text) }

                    fetMedicines.setError(state.medicineError)
                    fetDose.setError(state.doseError)
                    fetDoseUnit.setError(state.doseUnitError)
                    fetRoute.setError(state.routesError)
                    fetFrequency.setError(state.frequencyError)
                    fetDispenseQuantity.setError(state.dispenseQuantityError)
                    fetDispenseQuantityUnit.setError(state.quantityUnitError)

                    fetDynamicSig.apply {
                        isVisible = state.swSig
                        setText(state.dynamicSig)
                        setError(state.dynamicSigError)
                    }
                    swSig.isChecked = state.swSig

                    fetDose.isVisible = state.swSig.not()
                    fetDoseUnit.isVisible = state.swSig.not()
                    fetRoute.isVisible = state.swSig.not()
                    fetFrequency.isVisible = state.swSig.not()

                    if (state.swSig) {
                        fetDynamicSig.visible()
                    } else {
                        fetDynamicSig.gone()
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is CreatePrescriptionViewModel.NavigationEvents.NavigateToPrescriptionDetail -> {
                        /*val action =
                            CreatePrescriptionFragmentDirections.actionPrescriptionFragmentToPrescriptionDetailFragment(
                                event.patient
                            )
                        findNavController().navigate(action, navOptions { launchSingleTop = true })*/
                        setFragmentResult("refresh", bundleOf("refresh" to true))
                        findNavController().popBackStack()
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowDoseUnitDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Dose Unit"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnDoseSelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowRoutesDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Route"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnRouteSelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowQuantityDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Quantity"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnQuantitySelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowFrequencyDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Frequency"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnFrequencySelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowDiagnosisDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Diagnosis"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnDiagnosisSelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowRefillDialog -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Refill"
                        ) {
                            onEvent(CreatePrescriptionIntent.OnRefillSelected(it))
                        }
                    }

                    is CreatePrescriptionViewModel.NavigationEvents.ShowControlSubstanceDialog -> {
                        /*infoDialog =
                            errorDialog(ErrorModel(title = event.title, message = event.message)) {
                                infoDialog.dismiss()
                            }
                        infoDialog.show()*/
                        ConfirmationDialogFragment.show(requireActivity().supportFragmentManager) {
                            viewModel.onIntent(CreatePrescriptionIntent.OnControlSubstanceConfirm(it))
                        }
                    }
                }
            }
        }
    }

    override fun getMyViewModel() = viewModel
}