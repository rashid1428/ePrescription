package com.doctor.eprescription.features.homescreen.addeditpatient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.GetPharmacyRequest
import com.doctor.eprescription.data.remote.model.response.AddPatientRequest
import com.doctor.eprescription.data.remote.model.response.GetStateListResponse
import com.doctor.eprescription.data.remote.model.response.PatientsResponse
import com.doctor.eprescription.data.remote.model.response.Pharmacy
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import com.doctor.eprescription.extension.EMPTY
import com.doctor.eprescription.extension.millisToDate
import com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling.FormError
import com.doctor.eprescription.features.homescreen.addeditpatient.errorhandling.FormItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class AddEditPatientViewModel @Inject constructor(
    val patientRepo: PatientRepo,
    val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        const val TAG = "AddPatientViewModel"
    }

    private val _uiState = MutableStateFlow(AddEditPatientState())
    val uiState = _uiState.asStateFlow()

    private val patient = savedStateHandle.get<PatientsResponse.Patient?>("patient")

    private val _formError = MutableStateFlow<FormError>(FormError.Idle)
    val formError = _formError.asStateFlow()
    var formHasError = false

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    private val _uiEvents = Channel<AddEditPatientIntent>(Channel.UNLIMITED)
    private val validationArray: ArrayList<Boolean> = arrayListOf()

    init {
        handleIntent()

        if (patient != null) {
            _uiState.update { it.copy(title = "Update Patient") }
        } else {
            _uiState.update { it.copy(title = "Add Patient") }
        }

        setPatientData()
    }

    private fun setPatientData() {
        viewModelScope.launch {
            patient?.let { patient ->
                _uiState.update {
                    it.copy(
                        firstName = patient.firstName,
                        lastName = patient.lastName,
                        email = patient.email,
                        city = patient.city,
                        height = patient.height,
                        street = patient.street,
                        selectedHeightUnit = if (patient._heightUnit != null) SelectionModel(
                            id = if (patient.heightUnit == "cm") "1" else "2",
                            text = patient.heightUnit
                        ) else null,
                        weight = patient.weight,
                        selectedWeightUnit = if (patient._weightUnit != null) SelectionModel(
                            id = if (patient.weightUnit == "kg") "1" else "2",
                            text = patient.weightUnit
                        ) else null,
                        zipCode = patient.zipCode,
                        phone = patient.phone,
                        gender = if (patient.gender == "M") Gender.MALE else Gender.FEMALE,
                        selectedState = GetStateListResponse.State(
                            patient.state.id,
                            patient.state.name,
                            patient.state.abbreviation
                        ),
                        selectedPharmacy = Pharmacy(
                            id = patient.pharmacyId,
                            name = patient.pharmacyName,
                        ),
                        dobString = patient.dateOfBirth,
                        dobMillis = convertDateStringToMilliseconds(
                            patient.dateOfBirth,
                            "dd-MM-yyyy"
                        ),
                    )
                }
            }
        }
    }

    private fun convertDateStringToMilliseconds(dateString: String, format: String): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = sdf.parse(dateString)
        return date?.time ?: -1
    }

    private fun handleIntent() {
        viewModelScope.launch {
            _uiEvents.consumeAsFlow().collectLatest { intent ->
                when (intent) {
                    AddEditPatientIntent.OnStateClick -> {
                        getStateList()
                    }
                    is AddEditPatientIntent.OnStateSelected -> {
                        val selectedState =
                            _uiState.value.stateList.find { it.id.toString() == intent.item.id }
                        selectedState?.let { state ->
                            _uiState.update { it.copy(selectedState = state) }
                        }
                    }
                    AddEditPatientIntent.OnFemaleClick -> _uiState.update { it.copy(gender = Gender.FEMALE) }
                    AddEditPatientIntent.OnMaleClick -> _uiState.update { it.copy(gender = Gender.MALE) }
                    AddEditPatientIntent.OnDobClick -> {
                        if (_uiState.value.dobMillis == 0L) {
                            _channel.send(NavigationEvents.OpenDatePicker(1, 0, 1970))
                        } else {
                            val calendar = Calendar.getInstance()
                            calendar.time = Date(_uiState.value.dobMillis)
                            _channel.send(
                                NavigationEvents.OpenDatePicker(
                                    day = calendar.get(Calendar.DAY_OF_MONTH),
                                    month = calendar.get(Calendar.MONTH),
                                    year = calendar.get(Calendar.YEAR)
                                )
                            )
                        }
                    }
                    AddEditPatientIntent.OnPharmacyClick -> {
                        getPharmacies()
                    }

                    AddEditPatientIntent.OnHeightUnitClick -> {
                        _channel.send(
                            NavigationEvents.NavigateToHeightUnitSelection(
                                _uiState.value.heightUnits, _uiState.value.selectedHeightUnit
                            )
                        )
                    }
                    is AddEditPatientIntent.OnHeightUnitSelected -> _uiState.update {
                        it.copy(
                            selectedHeightUnit = intent.item
                        )
                    }
                    is AddEditPatientIntent.OnWeightUnitSelected -> _uiState.update {
                        it.copy(
                            selectedWeightUnit = intent.item
                        )
                    }
                    AddEditPatientIntent.OnWeightUnitClick -> {
                        _channel.send(
                            NavigationEvents.NavigateToWeightUnitSelection(
                                _uiState.value.weightUnits, _uiState.value.selectedWeightUnit
                            )
                        )
                    }
                    is AddEditPatientIntent.OnDobSelected -> {
                        val dobMillis = createMillis(
                            intent.dayOfMonth,
                            intent.month,
                            intent.year
                        )
                        _uiState.update {
                            it.copy(
                                dobMillis = dobMillis,
                                dobString = dobMillis.millisToDate("dd-MM-yyyy")
                            )
                        }
                    }
                    is AddEditPatientIntent.OnPharmacySelected -> {
                        _uiState.update { it.copy(selectedPharmacy = _uiState.value.pharmacies.find { it.id.toString() == intent.pharmacy.id }) }
                    }
                }
            }
        }
    }

    private fun createMillis(dayOfMonth: Int, month: Int, year: Int): Long {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }.also {
            return it.timeInMillis
        }

    }

    private fun getStateList() {

        suspend fun launchStateSelectionDialog() {
            val selectionList = _uiState.value.stateList.map {
                SelectionModel(id = it.id.toString(), text = it.name)
            }
            val selectedState = _uiState.value.selectedState
            val selectedItem = if (selectedState != null) SelectionModel(
                selectedState.id.toString(),
                selectedState.name
            ) else null
            _channel.send(
                NavigationEvents.NavigateToStateSelection(
                    selectionList,
                    selectedItem
                )
            )
        }

        viewModelScope.launch {
            if (_uiState.value.stateList.isNotEmpty()) {
                launchStateSelectionDialog()
            } else {
                showLoader()
                when (val call = patientRepo.getStateList()) {
                    is Resource.Error -> showError(ErrorModel("Error", call.error))
                    is Resource.Success -> {
                        hideLoader()
                        _uiState.update {
                            it.copy(stateList = call.data.data.states)
                        }
                        launchStateSelectionDialog()
                    }
                }
            }
        }
    }

    fun onUpdateClick(vararg formItems: FormItem) {
        validateForm(formItems).invokeOnCompletion {
            viewModelScope.launch {
                if (validationArray.all { it == true }) {
                    val addPatientRequest = AddPatientRequest(
                        firstName = _uiState.value.firstName,
                        lastName = _uiState.value.lastName,
                        gender = _uiState.value.gender.value,
                        city = _uiState.value.city,
                        street = _uiState.value.street,
                        stateId = _uiState.value.selectedState?.id.toString(),
                        pharmacyId = _uiState.value.selectedPharmacy?.id.toString(),
                        zipCode = _uiState.value.zipCode,
                        dateOfBirth = _uiState.value.dobString,
                        phone = _uiState.value.phone,
                        weight = _uiState.value.weight,
                        weightUnitId = _uiState.value.selectedWeightUnit?.id?.toInt() ?: 0,
                        height = _uiState.value.height,
                        heightUnitId = _uiState.value.selectedHeightUnit?.id?.toInt() ?: 0,
                        doctorId = cacheRepo.getLoginResponse().first()?.data?.id ?: 0
                    )

                    showLoader()

                    if (patient == null) {
                        //Add Patient Api Call
                        addPatientApi(addPatientRequest)
                    } else {
                        //Edit Patient Api call
                        editPatientApi(addPatientRequest)
                    }
                }
            }
        }
    }

    private suspend fun editPatientApi(addPatientRequest: AddPatientRequest) {
        when (val call =
            patientRepo.updatePatient(_uiState.value.email, addPatientRequest)) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                if (call.data.callStatus == "true") {
                    _channel.send(NavigationEvents.NavigateToPatientList(true, call.data.patient))
                } else {
                    showError(ErrorModel("Error", call.data.resultDesc))
                }
            }
        }
    }

    private suspend fun addPatientApi(addPatientRequest: AddPatientRequest) {
        when (val call =
            patientRepo.addPatient(_uiState.value.email, addPatientRequest)) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                if (call.data.callStatus == "true") {
                    _channel.send(NavigationEvents.NavigateToPatientList(true))
                } else {
                    showError(ErrorModel("Error", call.data.resultDesc))
                }
            }
        }
    }

    private fun validateForm(formItems: Array<out FormItem>) = viewModelScope.launch {
        validationArray.clear()
        formItems.forEach { formItem ->
            when (formItem) {
                is FormItem.FirstName -> {
                    validationArray.add(if (formItem.firstName.isEmpty()) {
                        _formError.emit(FormError.FirstNameError())
                        false
                    } else {
                        _formError.emit(FormError.FirstNameError(String.EMPTY))
                        _uiState.update { it.copy(firstName = formItem.firstName) }
                        true
                    })
                }
                is FormItem.LastName -> {
                    validationArray.add(if (formItem.lastName.isEmpty()) {
                        _formError.emit(FormError.LastNameError())
                        false
                    } else {
                        _formError.emit(FormError.LastNameError(String.EMPTY))
                        _uiState.update { it.copy(lastName = formItem.lastName) }
                        true
                    })
                }
                is FormItem.Email -> {
                    validationArray.add(if (formItem.email.isEmpty()) {
                        _formError.emit(FormError.EmailError())
                        false
                    } else {
                        _formError.emit(FormError.EmailError(String.EMPTY))
                        _uiState.update { it.copy(email = formItem.email) }
                        true
                    })
                }
                is FormItem.City -> {
                    validationArray.add(if (formItem.city.isEmpty()) {
                        _formError.emit(FormError.CityError())
                        false
                    } else {
                        _formError.emit(FormError.CityError(String.EMPTY))
                        _uiState.update { it.copy(city = formItem.city) }
                        true
                    })
                }
                is FormItem.Height -> {
                    validationArray.add(if (formItem.height.isEmpty()) {
                        _formError.emit(FormError.HeightError())
                        false
                    } else {
                        _formError.emit(FormError.HeightError(String.EMPTY))
                        _uiState.update { it.copy(height = formItem.height) }
                        true
                    })
                }
                is FormItem.PhoneNo -> {
                    validationArray.add(if (formItem.phoneNo.isEmpty()) {
                        _formError.emit(FormError.PhoneNoError())
                        false
                    } else {
                        _formError.emit(FormError.PhoneNoError(String.EMPTY))
                        _uiState.update { it.copy(phone = formItem.phoneNo) }
                        true
                    })
                }
                is FormItem.Street -> {
                    validationArray.add(if (formItem.street.isEmpty()) {
                        _formError.emit(FormError.StreetError())
                        false
                    } else {
                        _formError.emit(FormError.StreetError(String.EMPTY))
                        _uiState.update { it.copy(street = formItem.street) }
                        true
                    })
                }
                is FormItem.Weight -> {
                    validationArray.add(if (formItem.weight.isEmpty()) {
                        _formError.emit(FormError.WeightError())
                        false
                    } else {
                        _formError.emit(FormError.WeightError(String.EMPTY))
                        _uiState.update { it.copy(weight = formItem.weight) }
                        true
                    })
                }
                is FormItem.ZipCode -> {
                    validationArray.add(if (formItem.zipCode.isEmpty()) {
                        _formError.emit(FormError.ZipCodeError())
                        false
                    } else {
                        _formError.emit(FormError.ZipCodeError(String.EMPTY))
                        _uiState.update { it.copy(zipCode = formItem.zipCode) }
                        true
                    })
                }
            }
            delay(50)
        }
        validationArray.add(
            if (_uiState.value.selectedPharmacy == null) {
                _formError.emit(FormError.Pharmacy())
                false
            } else {
                _formError.emit(FormError.Pharmacy(String.EMPTY))
                true
            }
        )

        validationArray.add(
            if (_uiState.value.selectedState == null) {
                _formError.emit(FormError.StateError())
                false
            } else {
                _formError.emit(FormError.StateError(String.EMPTY))
                true
            }
        )

        validationArray.add(
            if (_uiState.value.selectedHeightUnit == null) {
                _formError.emit(FormError.HeightUnitError())
                false
            } else {
                _formError.emit(FormError.HeightUnitError(String.EMPTY))
                true
            }
        )

        validationArray.add(
            if (_uiState.value.selectedWeightUnit == null) {
                _formError.emit(FormError.WeightUnitError())
                false
            } else {
                _formError.emit(FormError.WeightUnitError(String.EMPTY))
                true
            }
        )

        validationArray.add(
            if (_uiState.value.dobMillis == 0L) {
                _formError.emit(FormError.DateOfBirthError())
                false
            } else {
                _formError.emit(FormError.DateOfBirthError(String.EMPTY))
                true
            }
        )

        (_uiState.value.gender == Gender.IDLE).also {
            _formError.emit(
                if (it) FormError.GenderError() else FormError.GenderError(
                    String.EMPTY
                )
            )
            validationArray.add(!it)
        }
    }

    private fun getPharmacies() {
        suspend fun launchPharmacySelectionDialog() {
            val selectedPharmacy = _uiState.value.selectedPharmacy
            val selectedItem = if (selectedPharmacy != null) SelectionModel(
                selectedPharmacy.id.toString(),
                selectedPharmacy.name
            ) else null
            _channel.send(
                NavigationEvents.NavigateToPharmacySelection(
                    _uiState.value.pharmacies.map {
                        SelectionModel(id = it.id.toString(), text = it.name)
                    },
                    selectedItem = selectedItem
                )
            )
        }

        viewModelScope.launch {
            if (_uiState.value.pharmacies.isNotEmpty()) {
                launchPharmacySelectionDialog()
            } else {
                showLoader()
                when (val call = patientRepo.getPharmacy(GetPharmacyRequest())) {
                    is Resource.Error -> showError(ErrorModel(title = "Error", call.error))
                    is Resource.Success -> {
                        hideLoader()
                        _uiState.update { it.copy(pharmacies = call.data.data.pharmacyData) }
                        launchPharmacySelectionDialog()
                    }
                }
            }
        }
    }

    fun onIntent(intent: AddEditPatientIntent) {
        viewModelScope.launch { _uiEvents.send(intent) }
    }

    sealed class NavigationEvents {
        data class NavigateToPharmacySelection(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class NavigateToStateSelection(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class NavigateToWeightUnitSelection(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class NavigateToHeightUnitSelection(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class OpenDatePicker(
            val day: Int,
            val month: Int,
            val year: Int
        ) : NavigationEvents()

        data class NavigateToPatientList(
            val refresh: Boolean,
            val patient: PatientsResponse.Patient? = null
        ) : NavigationEvents()
    }

}