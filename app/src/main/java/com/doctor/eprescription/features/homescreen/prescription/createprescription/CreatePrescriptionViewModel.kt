package com.doctor.eprescription.features.homescreen.prescription.createprescription

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import com.doctor.eprescription.components.listselection.SelectionModel
import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.AddPendingMedicineRequest
import com.doctor.eprescription.data.remote.model.request.GetRouteListRequest
import com.doctor.eprescription.data.remote.model.request.GetSigRequest
import com.doctor.eprescription.data.remote.model.request.UpdatePendingMedicineRequest
import com.doctor.eprescription.data.remote.model.response.*
import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.domain.repo.CacheRepo
import com.doctor.eprescription.domain.repo.PatientRepo
import com.doctor.eprescription.features.homescreen.prescription.createprescription.model.CompoundingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePrescriptionViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
    private val cacheRepo: CacheRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CreatePrescriptionState())
    val uiState = _uiState.asStateFlow()

    private val patientData = savedStateHandle.get<PatientsResponse.Patient?>("patient")
    private val medicine = savedStateHandle.get<MedicineData?>("medicine")
    private var isUpdateMedicine = false

    private val _uiIntent = Channel<CreatePrescriptionIntent>()

    private val _channel = Channel<NavigationEvents>()
    val channel = _channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val loginData = cacheRepo.getLoginResponse().first()
            emitState {
                copy(
                    loginData = loginData,
                    compoundingList = listOf(
                        CompoundingModel(medicine = "", quantity = 0, quantityUnit = ""),
                    )
                )
            }

            medicine?.let {
                isUpdateMedicine = true
                emitState {
                    copy(
                        doseText = it.dosage,
                        specialInstructions = it.specialInstructions,
                        title = it.title,
                        swAddToMyTemplate = it.addToTemplate,
                        swMaySubstitute = it.maySubstitute.toInt() == 0,
                        swPRN = it.prn,
                        swFreeTextMedicine = it.freeText == 1,
                        selectedQuantity = GetQuantitiesResponse.Quantity(
                            it.quantityUnitId.toInt(),
                            it.quantityUnit,
                            ""
                        ),
                        dispenseQuantityText = it.quantity,
                        selectedMedicine = GetMedicineResponse.Medicine(
                            id = it.medicineId.toString(),
                            name = it.medicineName
                        ),
                        selectedDoseUnit = GetDoseListResponse.Dose(
                            id = it.doseUnit.toInt(),
                            it.doseUnitName
                        ),
                        selectedRoute = GetRouteListResponse.Route(it.route.toInt(), it.routeName),
                        selectedDirection = GetDirectionResponse.Direction(
                            it.direction.toInt(),
                            it.directionName
                        ),
                        selectedRefill = SelectionModel(id = it.refill, text = it.refill),
                        selectedIcdCode = if (it.icdCode != null) GetIcdCodeListResponse.IcdCode(
                            id = it.icdCodeId,
                            code = it.icdCode.orEmpty(),
                            description = it.icdCodeDescription.orEmpty()
                        ) else null,
                        dynamicSig = it.sig,
                        swSig = it.doseUnit == "-1"
                    )
                }
            }
        }

        handleIntent()
    }

    private suspend fun getIcdCodeList(onGet: (List<GetIcdCodeListResponse.IcdCode>) -> Unit) {
        if (_uiState.value.icdCodes.isNotEmpty()) {
            onGet(_uiState.value.icdCodes)
            return
        }
        showLoader()
        when (val call = patientRepo.getIcdCodeList()) {
            is Resource.Error -> showError(ErrorModel("Error", call.error))
            is Resource.Success -> {
                hideLoader()
                if (call.data.callStatus == "true") {
                    onGet(call.data.data?.icdCodes ?: emptyList())
                } else {
                    showError(ErrorModel("Error", call.data.resultDesc))
                }
            }
        }
    }

    private suspend fun getDoseList(onGet: (List<GetDoseListResponse.Dose>) -> Unit) {
        if (_uiState.value.doseList.isNotEmpty()) {
            onGet(_uiState.value.doseList)
            return
        }

        showLoader()
        when (val call = patientRepo.getDoseList()) {
            is Resource.Error -> showError(ErrorModel(title = "Error", message = call.error))
            is Resource.Success -> {
                hideLoader()
                onGet(call.data.data.dose)
                /*emitState {
                    copy(doseList = call.data.data.dose)
                }
                getRoutes()*/
            }
        }
    }

    private suspend fun getRoutes(onGet: (List<GetRouteListResponse.Route>) -> Unit) {
        if (_uiState.value.routes.isNotEmpty()) {
            onGet(_uiState.value.routes)
            return
        }
        viewModelScope.launch {
            showLoader()
            val loginResponse = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.getRouteList(
                GetRouteListRequest(
                    patientId = patientData?.patientId.toString(),
                    doctorId = loginResponse?.data?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    onGet(call.data.data.routes)
                }
            }
        }
    }

    private suspend fun getQuantityList(onGet: (List<GetQuantitiesResponse.Quantity>) -> Unit) {
        if (_uiState.value.quantities.isNotEmpty()) {
            onGet(_uiState.value.quantities)
            return
        }
        viewModelScope.launch {
            showLoader()
            val loginResponse = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.getQuantityList(
                GetRouteListRequest(
                    patientId = patientData?.patientId.toString(),
                    doctorId = loginResponse?.data?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    onGet(call.data.data.quantities)
                }
            }
        }
    }

    private suspend fun getFrequency(onGet: (List<GetDirectionResponse.Direction>) -> Unit) {
        if (_uiState.value.directions.isNotEmpty()) {
            onGet(_uiState.value.directions)
            return
        }
        viewModelScope.launch {
            showLoader()
            val loginResponse = cacheRepo.getLoginResponse().first()
            when (val call = patientRepo.getDirections(
                GetRouteListRequest(
                    patientId = patientData?.patientId.toString(),
                    doctorId = loginResponse?.data?.id.toString()
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    onGet(call.data.data.directions)
                }
            }
        }
    }

    private fun getRefills(onGet: (List<SelectionModel>) -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.refillList.isNotEmpty()) {
                onGet(_uiState.value.refillList)
                return@launch
            }

            val refills = (0..99).map {
                SelectionModel(id = it.toString(), text = it.toString())
            }
            onGet(refills)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            _uiIntent.consumeAsFlow().collectLatest { intent ->
                when (intent) {
                    is CreatePrescriptionIntent.OnDoseTextChanged -> {
                        /*emitState {
                            copy(
                                doseError = if (intent.text.isNotEmpty()) "" else "Dose is mandatory"
                            )
                        }*/
                    }

                    is CreatePrescriptionIntent.OnCompoundingCheck -> {
                        emitState {
                            copy(
                                cbCompounding = intent.isChecked,
                                cbMedicalSupply = if (intent.isChecked) false else cbMedicalSupply,
                                swFreeTextMedicine = if (intent.isChecked) false else swFreeTextMedicine
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnMedicalSupplyCheck -> {
                        emitState {
                            copy(
                                swFreeTextMedicine = if (intent.isChecked) false else this.swFreeTextMedicine,
                                cbCompounding = if (intent.isChecked) false else cbCompounding,
                                cbMedicalSupply = intent.isChecked
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnFreeTextMedicineSwitch -> {
                        emitState {
                            copy(
                                swFreeTextMedicine = intent.isChecked
                            )
                        }
                        if (intent.isChecked) {
                            _channel.send(
                                NavigationEvents.ShowControlSubstanceDialog(
                                    title = "Are you sure?",
                                    message = "Drugs one or more compound ingredients contain controlled substance?"
                                )
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnAddCompoundingItem -> {
                        /*val newList = ArrayList(uiState.value.compoundingList)
                        val size = newList.size
                        newList.add(CompoundingModel("", 0,))*/

                        emitState {
                            copy(
                                compoundingList = compoundingList.plus(CompoundingModel())
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnRemoveCompoundingItem -> {
                        emitState {
                            copy(
                                compoundingList = compoundingList.minusElement(compoundingList[intent.position])
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnDoseUnitClick -> {
                        getDoseList { doseList ->
                            emitState { copy(doseList = doseList) }
                            val selectionList = _uiState.value.doseList.map {
                                SelectionModel(id = it.id.toString(), text = it.name)
                            }
                            val selectedDoseUnit = _uiState.value.selectedDoseUnit
                            val selectedItem = if (selectedDoseUnit != null) SelectionModel(
                                selectedDoseUnit.id.toString(),
                                selectedDoseUnit.name
                            ) else null

                            sendChannelEvent(
                                NavigationEvents.ShowDoseUnitDialog(
                                    selectionList,
                                    selectedItem
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnDoseSelected -> {
                        emitState {
                            copy(
                                selectedDoseUnit = _uiState.value.doseList.find { it.id.toString() == intent.item.id },
                                doseUnitError = ""
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnRouteClick -> {
                        getRoutes { routes ->
                            emitState { copy(routes = routes) }
                            val selectionList = routes.map {
                                SelectionModel(id = it.id.toString(), text = it.name)
                            }
                            val selectedRoute = _uiState.value.selectedRoute
                            val selectedItem = if (selectedRoute != null) SelectionModel(
                                selectedRoute.id.toString(),
                                selectedRoute.name
                            ) else null

                            sendChannelEvent(
                                NavigationEvents.ShowRoutesDialog(
                                    selectionList,
                                    selectedItem
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnRouteSelected -> {
                        emitState {
                            copy(
                                selectedRoute = _uiState.value.routes.find { it.id.toString() == intent.item.id },
                                routesError = ""
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnQuantityClick -> {
                        getQuantityList { quantities ->
                            emitState { copy(quantities = quantities) }
                            val selectionList = quantities.map {
                                SelectionModel(id = it.id.toString(), text = it.name)
                            }
                            val selectedRoute = _uiState.value.selectedQuantity
                            val selectedItem = if (selectedRoute != null) SelectionModel(
                                selectedRoute.id.toString(),
                                selectedRoute.name
                            ) else null

                            sendChannelEvent(
                                NavigationEvents.ShowQuantityDialog(
                                    selectionList,
                                    selectedItem
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnQuantitySelected -> {
                        emitState {
                            copy(
                                selectedQuantity = _uiState.value.quantities.find { it.id.toString() == intent.item.id },
                                quantityUnitError = ""
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnFrequencyClick -> {
                        getFrequency { directions ->
                            emitState { copy(directions = directions) }
                            val selectionList = directions.map {
                                SelectionModel(id = it.id.toString(), text = it.name)
                            }
                            val selectedDirection = _uiState.value.selectedDirection
                            val selectedItem = if (selectedDirection != null) SelectionModel(
                                selectedDirection.id.toString(),
                                selectedDirection.name
                            ) else null

                            sendChannelEvent(
                                NavigationEvents.ShowFrequencyDialog(
                                    selectionList,
                                    selectedItem
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnFrequencySelected -> {
                        emitState {
                            copy(
                                selectedDirection = _uiState.value.directions.find { it.id.toString() == intent.item.id },
                                frequencyError = ""
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnNextClick -> {
                        val medicineError =
                            if (_uiState.value.swFreeTextMedicine) {
                                if (intent.freeText.isEmpty()) {
                                    "Medicine Name is mandatory"
                                } else ""
                            } else {
                                if (_uiState.value.selectedMedicine == null) "Medicine is mandatory" else ""
                            }
                        emitState {
                            copy(
                                dispenseQuantityText = intent.dispenseQuantityText,
                                specialInstructions = intent.specialInstructions,
                                doseText = intent.doseText,
                                dynamicSig = intent.dynamicSig,
                                freeText = intent.freeText,

                                medicineError = medicineError,
                                quantityTextError = if (intent.dispenseQuantityText.isEmpty()) "Quantity is mandatory" else "",
                                doseError = if (intent.doseText.isEmpty() && !_uiState.value.swSig) "Dose is mandatory" else "",
                                doseUnitError = if (_uiState.value.selectedDoseUnit == null && !_uiState.value.swSig) "Dose Unit is mandatory" else "",
                                routesError = if (_uiState.value.selectedRoute == null) "Route is mandatory" else "",
                                frequencyError = if (_uiState.value.selectedDirection == null && !_uiState.value.swSig) "Frequency is mandatory" else "",
                                dynamicSigError = if (_uiState.value.swSig && intent.dynamicSig.isEmpty()) {
                                    "Dynamic Sig is mandatory"
                                } else ""
                            )
                        }.invokeOnCompletion {
                            if (isValid()) {
                                if (isUpdateMedicine) {
                                    updatePendingMedicine()
                                } else {
                                    addPendingMedicine()
                                }
                            }
                        }
                    }

                    is CreatePrescriptionIntent.OnMedicineSelected -> {
                        emitState {
                            copy(
                                selectedMedicine = intent.medicine,
                                medicineError = ""
                            )
                        }
                        getSigApi(intent.medicine)
                    }

                    is CreatePrescriptionIntent.OnAddToMyTemplateCheck -> emitState {
                        copy(swAddToMyTemplate = intent.isChecked)
                    }

                    is CreatePrescriptionIntent.OnMaySubstituteCheck -> emitState {
                        copy(swMaySubstitute = intent.isChecked)
                    }

                    CreatePrescriptionIntent.OnSkip -> {
                        viewModelScope.launch {
                            _channel.send(NavigationEvents.NavigateToPrescriptionDetail(patientData))
                        }
                    }

                    CreatePrescriptionIntent.OnDiagnosisClick -> {
                        getIcdCodeList { icdCodes ->
                            emitState { copy(icdCodes = icdCodes) }
                            val selectionList = icdCodes.map {
                                SelectionModel(id = it.id.toString(), text = it.getFullText)
                            }.sortedBy { it.text }
                            val selectedRoute = _uiState.value.selectedIcdCode
                            val selectedItem = if (selectedRoute != null) SelectionModel(
                                selectedRoute.id.toString(),
                                selectedRoute.getFullText
                            ) else null

                            sendChannelEvent(
                                NavigationEvents.ShowDiagnosisDialog(
                                    selectionList,
                                    selectedItem
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnDiagnosisSelected -> {
                        emitState {
                            copy(
                                selectedIcdCode = _uiState.value.icdCodes.find { it.id.toString() == intent.item.id }
                            )
                        }
                    }

                    CreatePrescriptionIntent.OnRefillClick -> {
                        getRefills { refills ->
                            emitState { copy(refillList = refills) }
                            val selectedRoute = _uiState.value.selectedRefill

                            sendChannelEvent(
                                NavigationEvents.ShowRefillDialog(
                                    refills,
                                    selectedRoute
                                )
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnRefillSelected -> {
                        emitState {
                            copy(
                                selectedRefill = _uiState.value.refillList.find { it.id == intent.item.id }
                            )
                        }
                    }

                    is CreatePrescriptionIntent.OnSigSwitch -> emitState { copy(swSig = intent.isChecked) }
                    is CreatePrescriptionIntent.OnControlSubstanceConfirm -> {
                        emitState { copy(swFreeTextMedicine = intent.confirm) }
                    }
                }
            }
        }
    }

    private fun getSigApi(medicine: GetMedicineResponse.Medicine) {
        viewModelScope.launch {
            showLoader()
            val doctorId = cacheRepo.getLoginResponse().first()?.data?.id
            when (val call = patientRepo.getSig(
                GetSigRequest(
                    doctorId = doctorId.toString(),
                    medicineId = medicine.id,
                    medicineName = medicine.name
                )
            )) {
                is Resource.Error -> {
                    showError(ErrorModel("Error", call.error))
                }

                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        emitState {
                            copy()
                        }
                    } else {
                        showError(ErrorModel("Error", call.data.resultDesc))
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        val state = _uiState.value
        if (state.swSig) {
            return state.medicineError.isEmpty()
                    && state.dynamicSigError.isEmpty()
                    && state.refillError.isEmpty()
                    && state.dispenseQuantityError.isEmpty()
                    && state.routesError.isEmpty()
                    && state.frequencyError.isEmpty()
        }

        return state.medicineError.isEmpty()
                && state.doseError.isEmpty()
                && state.doseUnitError.isEmpty()
                && state.routesError.isEmpty()
                && state.frequencyError.isEmpty()
                && state.refillError.isEmpty()
                && state.dispenseQuantityError.isEmpty()
                && state.routesError.isEmpty()
                && state.frequencyError.isEmpty()
    }

    private fun addPendingMedicine() {
        viewModelScope.launch {
            showLoader()
            val state = _uiState.value
            val medicine = AddPendingMedicineRequest.Medicine(
                isControlledSubstance = if (state.swFreeTextMedicine) "1" else state.selectedMedicine!!.isControlledSubstance.toString(),
                title = "",
                medicineId = if (state.swFreeTextMedicine) "-1" else state.selectedMedicine!!.id,
                medicineName = if (state.swFreeTextMedicine) state.freeText else state.selectedMedicine!!.name,
                dosage = if (state.swSig) "" else state.doseText,
                doseUnit = if (state.swSig) "-1" else state.selectedDoseUnit?.id.toString(),
                route = if (state.swSig) "-1" else state.selectedRoute?.id.toString(),
                direction = if (state.swSig) "-1" else state.selectedDirection?.id.toString(),
                quantity = state.dispenseQuantityText,
                totalQuantityUnit = state.selectedQuantity?.id.toString(),
                strengthFormId = "",
                icdCodeId = state.selectedIcdCode?.id.toString() ?: "",
                refill = state.selectedRefill?.id ?: "",
                maySubstitute = if (state.swMaySubstitute) 0 else 1,
                addToTemplate = if (state.swAddToMyTemplate) 1 else 0,
                prn = 0,
                specialInstructions = state.specialInstructions,
                freeText = if (state.swFreeTextMedicine) 1 else 0,
                _sig = "",
                compoundMedicinesList = emptyList()
            )

            /*
            * requestType =
            * both uncheck = 1
            * compounding check = 3
            * medical supply check = 2
            * */

            when (val call = patientRepo.addPendingMedicine(
                AddPendingMedicineRequest(
                    doctorId = state.loginData?.data?.id ?: 0,
                    patientId = patientData?.patientId ?: 0,
                    requestType = when {
                        state.cbCompounding -> 3
                        state.cbMedicalSupply -> 2
                        else -> 1
                    },
                    medicineList = listOf(medicine)
                )
            )) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _channel.send(NavigationEvents.NavigateToPrescriptionDetail(patientData!!))
                    } else {
                        showError(ErrorModel(title = "Error", message = call.data.resultDesc))
                    }
                }
            }
        }
    }

    private fun updatePendingMedicine() {
        viewModelScope.launch {
            showLoader()
            val state = _uiState.value
            val request = UpdatePendingMedicineRequest(
                addToTemplate = if (state.swAddToMyTemplate) 1 else 0,
                direction = if (state.swSig) "-1" else state.selectedDirection?.id.toString(),
                dosage = if (state.swSig) "" else state.doseText,
                doseUnit = if (state.swSig) "-1" else state.selectedDoseUnit?.id.toString(),
                freeText = 0,
                icdCodeId = state.selectedIcdCode?.id.toString() ?: "",
                isControlledSubstance = state.selectedMedicine!!.isControlledSubstance.toString(),
                isPendingMedicine = 0,
                maySubstitute = if (state.swMaySubstitute) 0 else 1,
                medicineId = state.selectedMedicine.id,
                medicineName = state.selectedMedicine.name,
                pendingMedicineId = "",
                prn = 0,
                quantity = state.dispenseQuantityText,
                refill = state.selectedRefill?.id ?: "",
                requestType = when {
                    state.cbCompounding -> 3
                    state.cbMedicalSupply -> 2
                    else -> 1
                },
                route = if (state.swSig) "-1" else state.selectedRoute?.id.toString(),
                sig = "",
                specialInstructions = state.specialInstructions,
                strengthFormId = null,
                title = "",
                totalQuantityUnit = state.selectedQuantity?.id.toString(),
                compoundMedicinesList = emptyList()
            )

            when (val call = patientRepo.updatePendingMedicine(request)) {
                is Resource.Error -> showError(ErrorModel("Error", call.error))
                is Resource.Success -> {
                    hideLoader()
                    if (call.data.callStatus == "true") {
                        _channel.send(NavigationEvents.NavigateToPrescriptionDetail(patientData!!))
                    } else {
                        showError(ErrorModel(title = "Error", message = call.data.resultDesc))
                    }
                }
            }
        }
    }

    private fun emitState(action: CreatePrescriptionState.() -> CreatePrescriptionState) =
        viewModelScope.launch {
            _uiState.emit(action(_uiState.value))
        }

    private fun sendChannelEvent(event: NavigationEvents) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

    fun onIntent(intent: CreatePrescriptionIntent) {
        viewModelScope.launch { _uiIntent.send(intent) }
    }

    sealed class NavigationEvents {
        data class NavigateToPrescriptionDetail(val patient: PatientsResponse.Patient?) :
            NavigationEvents()

        data class ShowDoseUnitDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowRoutesDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowQuantityDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowFrequencyDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowDiagnosisDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowRefillDialog(
            val list: List<SelectionModel>,
            val selectedItem: SelectionModel?
        ) : NavigationEvents()

        data class ShowControlSubstanceDialog(
            val title: String,
            val message: String
        ) : NavigationEvents()
    }
}