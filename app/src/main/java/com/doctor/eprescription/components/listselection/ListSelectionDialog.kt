package com.doctor.eprescription.components.listselection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.eprescription.basefragment.FullScreenDialogFragment
import com.doctor.eprescription.databinding.DialogSearchSelectBinding
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.extension.putParcelableList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
open class ListSelectionDialog :
    FullScreenDialogFragment<DialogSearchSelectBinding>(DialogSearchSelectBinding::inflate) {

    private val viewModel by viewModels<SelectionViewModel>()
    private val mAdapter by lazy { SelectionAdapter() }
    private var action: ((item: SelectionModel) -> Unit)? = null
    private var actionMulti: ((item: List<SelectionModel>) -> Unit)? = null

    companion object {

        const val ARGS_HEADING = "ARG_HEADING"
        const val ARGS_MULTIPLE_SELECTION = "ARGS_MULTIPLE_SELECTION"
        const val ARGS_LIST = "ARG_LIST"
        const val ARGS_SELECTION_MODEL = "ARGS_SELECTION_MODEL"
        const val ARGS_SELECTED_ITEMS = "ARGS_SELECTED_ITEMS"

        fun show(
            fragmentManager: FragmentManager,
            list: List<SelectionModel>,
            selectedItem: SelectionModel? = null,
            heading: String,
            onItemSelected: (item: SelectionModel) -> Unit
        ) {
            val dialog = ListSelectionDialog()
            dialog.action = onItemSelected
            dialog.arguments = Bundle().apply {
                putParcelableArrayList(
                    ARGS_LIST,
                    ArrayList<SelectionModel>().apply { addAll(list) })
                putParcelable(ARGS_SELECTION_MODEL, selectedItem)
                putString(ARGS_HEADING, heading)
            }
            dialog.show(fragmentManager, "ListSelectionDialog")
        }

        fun showForMultiSelection(
            fragmentManager: FragmentManager,
            list: List<SelectionModel>,
            selectedItem: List<SelectionModel>? = null,
            heading: String,
            onItemsSelected: (item: List<SelectionModel>) -> Unit
        ) {
            val dialog = ListSelectionDialog()
            dialog.actionMulti = onItemsSelected
            dialog.arguments = Bundle().apply {
                putParcelableArrayList(
                    ARGS_LIST,
                    ArrayList<SelectionModel>().apply { addAll(list) })
                selectedItem?.let {
                    putParcelableList(ARGS_SELECTED_ITEMS, it)
                }
                putString(ARGS_HEADING, heading)
                putBoolean(ARGS_MULTIPLE_SELECTION, true)
            }
            dialog.show(fragmentManager, "ListSelectionDialog")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvCommonInfoList.layoutManager = LinearLayoutManager(requireContext())
            rvCommonInfoList.adapter = mAdapter
            rvCommonInfoList.itemAnimator = null

            backer.root.setOnClickListener { dismiss() }

            mAdapter.setSelectionCallback {
                viewModel.onItemSelected(it)
            }

            etSearch.doOnTextChanged { text, start, before, count ->
                viewModel.search(text.toString())
            }

            doneButton.setOnClickListener {
                if (actionMulti != null) {
                    actionMulti?.invoke(viewModel.getSelectedItems())
                } else {
                    action?.invoke(viewModel.getSelectedItem()!!)
                }
                dismiss()
            }
        }

        collectWhenStarted {
            viewModel.showDoneButton.collectLatest { binding.doneButton.isVisible = it }
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

    override fun getMyViewModel() = viewModel
}