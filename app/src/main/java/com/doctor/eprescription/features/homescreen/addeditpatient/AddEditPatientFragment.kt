package com.doctor.eprescription.features.homescreen.addeditpatient

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.components.listselection.ListSelectionDialog
import com.doctor.eprescription.databinding.FragmentAddEditPatientBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.extension.setTextNotEmpty
import com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling.FormError
import com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling.FormItem
import com.doctor.eprescription.helper.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class AddEditPatientFragment : BaseFragmentVB<FragmentAddEditPatientBinding>(
    FragmentAddEditPatientBinding::inflate
) {

    companion object {
        const val TAG = "AddEditPatientFragment"
    }

    private val viewModel by viewModels<AddEditPatientViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpClickListeners()
        addObservers()
    }

    private fun onEvent(intent: AddEditPatientIntent) {
        viewModel.onIntent(intent)
    }

    private fun setUpClickListeners() {
        binding.apply {
            compBackButton.root.setOnClickListener {
                findNavController().popBackStack()
            }

            btnMale.setOnClickListener {
                viewModel.onIntent(AddEditPatientIntent.OnMaleClick)
            }

            btnFemale.setOnClickListener {
                viewModel.onIntent(AddEditPatientIntent.OnFemaleClick)
            }

            ddPharmacy.actAsClickItem {
                viewModel.onIntent(AddEditPatientIntent.OnPharmacyClick)
            }

            ddDateOfBirth.actAsClickItem {
                viewModel.onIntent(AddEditPatientIntent.OnDobClick)
            }

            fetState.actAsClickItem {
                viewModel.onIntent(AddEditPatientIntent.OnStateClick)
            }

            fetHeightUnit.actAsClickItem {
                viewModel.onIntent(AddEditPatientIntent.OnHeightUnitClick)
            }

            fetWeightUnit.actAsClickItem {
                viewModel.onIntent(AddEditPatientIntent.OnWeightUnitClick)
            }

            btnUpdate.setOnClickListener {
                viewModel.onUpdateClick(
                    FormItem.FirstName(fetFirstName.getText()),
                    FormItem.LastName(fetLastName.getText()),
                    FormItem.Email(fetEmail.getText()),
                    FormItem.PhoneNo(fetPhone.getText()),
                    FormItem.Street(fetStreet.getText()),
                    FormItem.City(fetCity.getText()),
                    FormItem.ZipCode(fetZipCode.getText()),
                    FormItem.Weight(fetWeight.getText()),
                    FormItem.Height(fetHeight.getText()),
                )
            }
        }
    }

    private fun addObservers() {
        collectWhenStarted {
            viewModel.formError.collect { formError ->
                Log.e("Error", formError.toString())
                when (formError) {
                    FormError.Idle -> {}
                    is FormError.FirstNameError -> binding.fetFirstName.setError(formError.error)
                    is FormError.LastNameError -> binding.fetLastName.setError(formError.error)
                    is FormError.EmailError -> binding.fetEmail.setError(formError.error)
                    is FormError.CityError -> binding.fetCity.setError(formError.error)
                    is FormError.DateOfBirthError -> binding.ddDateOfBirth.setError(formError.error)
                    is FormError.GenderError -> {
                        binding.tvGenderError.isVisible = formError.error.isNotEmpty()
                    }
                    is FormError.HeightError -> binding.fetHeight.setError(formError.error)
                    is FormError.HeightUnitError -> binding.fetHeightUnit.setError(formError.error)
                    is FormError.Pharmacy -> binding.ddPharmacy.setError(formError.error)
                    is FormError.PhoneNoError -> binding.fetPhone.setError(formError.error)
                    is FormError.StateError -> binding.fetState.setError(formError.error)
                    is FormError.StreetError -> binding.fetStreet.setError(formError.error)
                    is FormError.WeightError -> binding.fetWeight.setError(formError.error)
                    is FormError.WeightUnitError -> binding.fetWeightUnit.setError(formError.error)
                    is FormError.ZipCodeError -> binding.fetZipCode.setError(formError.error)
                }
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is AddEditPatientViewModel.NavigationEvents.NavigateToPharmacySelection -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Pharmacy"
                        ) {
                            onEvent(AddEditPatientIntent.OnPharmacySelected(it))
                        }
                    }
                    is AddEditPatientViewModel.NavigationEvents.NavigateToStateSelection -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "State"
                        ) {
                            onEvent(AddEditPatientIntent.OnStateSelected(it))
                        }
                    }
                    is AddEditPatientViewModel.NavigationEvents.OpenDatePicker -> {
                        showDatePickerDialog(
                            activity = requireActivity(),
                            selectedDay = event.day.toString(),
                            selectedMonth = event.month.toString(),
                            selectedYear = event.year.toString(),
                            onDoneClick = { day, month, year ->
                                viewModel.onIntent(
                                    AddEditPatientIntent.OnDobSelected(
                                        day.toInt(),
                                        month.toInt(),
                                        year.toInt()
                                    )
                                )
                            },
                            onCancel = {

                            }
                        )

                        /*val dateSetListener =
                            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                                binding.ddDateOfBirth.setText("$dayOfMonth/${month + 1}/$year")
                                viewModel.onIntent(
                                    AddEditPatientIntent.OnDobSelected(
                                        dayOfMonth,
                                        month,
                                        year
                                    )
                                )
                            }
                        DatePickerDialog(
                            requireContext(),
                            dateSetListener,
                            event.year,
                            event.month,
                            event.day
                        ).show()*/
                    }
                    is AddEditPatientViewModel.NavigationEvents.NavigateToHeightUnitSelection -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Height Unit"
                        ) {
                            onEvent(AddEditPatientIntent.OnHeightUnitSelected(it))
                        }
                    }
                    is AddEditPatientViewModel.NavigationEvents.NavigateToWeightUnitSelection -> {
                        ListSelectionDialog.show(
                            fragmentManager = requireActivity().supportFragmentManager,
                            list = event.list,
                            selectedItem = event.selectedItem,
                            heading = "Weight Unit"
                        ) {
                            onEvent(AddEditPatientIntent.OnWeightUnitSelected(it))
                        }
                    }
                    is AddEditPatientViewModel.NavigationEvents.NavigateToPatientList -> {
                        setFragmentResult("refresh", bundleOf("refresh" to event.refresh))
                        event.patient?.let {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "patient",
                                it
                            )
                        }
                        findNavController().popBackStack()
                    }
                }
            }
        }

        collectWhenStarted {
            viewModel.uiState.collectLatest { state ->
//                binding.tvGenderError.isVisible = state.gender == Gender.IDLE

                when (state.gender) {
                    Gender.MALE -> {
                        binding.btnMale.isSelected = true
                        binding.btnFemale.isSelected = false
                    }
                    Gender.FEMALE -> {
                        binding.btnMale.isSelected = false
                        binding.btnFemale.isSelected = true
                    }
                    Gender.IDLE -> {
                        binding.btnMale.isSelected = false
                        binding.btnFemale.isSelected = false
                    }
                }

                binding.apply {
                    tvTitle.text = state.title

                    ddDateOfBirth.setTextNotEmpty(state.dobString)
                    ddPharmacy.setTextNotEmpty(state.selectedPharmacy?.name.orEmpty())
                    fetFirstName.setTextNotEmpty(state.firstName)
                    fetLastName.setTextNotEmpty(state.lastName)
                    fetEmail.setTextNotEmpty(state.email)
                    fetZipCode.setTextNotEmpty(state.zipCode)
                    fetPhone.setTextNotEmpty(state.phone)
                    fetStreet.setTextNotEmpty(state.street)
                    fetCity.setTextNotEmpty(state.city)

                    state.selectedWeightUnit?.let { fetWeightUnit.setText(it.text) }
                    fetWeight.setTextNotEmpty(state.weight)

                    state.selectedHeightUnit?.let { fetHeightUnit.setText(it.text) }
                    fetHeight.setTextNotEmpty(state.height)

                    state.selectedState?.let { fetState.setText(it.name) }

                    fetWeightUnit.setText(state.selectedWeightUnit?.text.orEmpty())
                    fetHeightUnit.setText(state.selectedHeightUnit?.text.orEmpty())
                }
            }
        }
    }

    override fun getMyViewModel() = viewModel
}