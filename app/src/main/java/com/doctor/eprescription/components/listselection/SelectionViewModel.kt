package com.doctor.eprescription.components.listselection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doctor.eprescription.baseviewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private var fullList: List<SelectionModel> = emptyList()
    private var query = ""
    private var mSelectedItem: SelectionModel? = null
    private var mSelectedItemsList: List<SelectionModel> = emptyList()
    private var isMultiSelection = false

    private var _list = MutableStateFlow<List<SelectionModel>>(emptyList())
    val list = _list.asStateFlow()

    protected val _heading = MutableStateFlow("")
    val heading = _heading.asStateFlow()

    private val _showDoneButton = MutableStateFlow(false)
    val showDoneButton = _showDoneButton.asStateFlow()

    init {
        viewModelScope.launch {
            _heading.emit(savedStateHandle.get<String>(ListSelectionDialog.ARGS_HEADING).orEmpty())
            isMultiSelection =
                savedStateHandle.get<Boolean>(ListSelectionDialog.ARGS_MULTIPLE_SELECTION) ?: false
            fullList = savedStateHandle.get<List<SelectionModel>>(ListSelectionDialog.ARGS_LIST)
                ?: emptyList()

            if (isMultiSelection) {
                mSelectedItemsList =
                    savedStateHandle.get<List<SelectionModel>>(ListSelectionDialog.ARGS_SELECTED_ITEMS)
                        ?: emptyList()
                fullList = fullList.map {
                    if (mSelectedItemsList.contains(it)) {
                        it.copy(isSelected = true)
                    } else it
                }
            } else {
                mSelectedItem =
                    savedStateHandle.get<SelectionModel>(ListSelectionDialog.ARGS_SELECTION_MODEL)

                fullList = fullList.map {
                    if (mSelectedItem != null && mSelectedItem?.id == it.id) {
                        it.copy(isSelected = true)
                    } else {
                        it.copy(isSelected = false)
                    }
                }
            }
            _list.emit(fullList)
            _showDoneButton.emit(list.value.any { it.isSelected })
        }
    }

    open fun search(query: String) {
        this@SelectionViewModel.query = query
        viewModelScope.launch {
            if (isMultiSelection) {
                _list.emit(
                    fullList
                        .filter { it.text.contains(query) })
            } else {
                _list.emit(
                    fullList
                        .filter { it.text.contains(query) }
                        .map {
                            if (it == mSelectedItem) it.copy(isSelected = true)
                            else it.copy(isSelected = false)
                        }
                )
            }
        }
    }

    fun getSelectedItem() = mSelectedItem
    fun getSelectedItems() = list.value.filter { it.isSelected }

    open fun onItemSelected(model: SelectionModel) {
        if (isMultiSelection) {
            doMultiSelection(model)
        } else {
            doSingleSelection(model)
        }
    }

    private fun doMultiSelection(model: SelectionModel) {
        viewModelScope.launch {
            fullList = fullList.map {
                if (it == model) it.copy(isSelected = it.isSelected.not())
                else it
            }

            _list.emit(fullList.filter { it.text.contains(query, true) })

            _showDoneButton.emit(list.value.any { it.isSelected })
        }
    }

    private fun doSingleSelection(model: SelectionModel) {
        if (model.isSelected) return
        mSelectedItem = model
        viewModelScope.launch {
            _list.emit(
                fullList
                    .filter { it.text.contains(query) }
                    .map {
                        if (it == model) it.copy(isSelected = true)
                        else it.copy(isSelected = false)
                    }
            )

            _showDoneButton.emit(list.value.any { it.isSelected })
        }
    }
}