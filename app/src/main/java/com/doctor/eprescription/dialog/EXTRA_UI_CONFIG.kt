package com.doctor.eprescription.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.doctor.eprescription.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.CustomInjection.inject

private const val EXTRA_UI_CONFIG = "EXTRA_UI_CONFIG"
/*

@AndroidEntryPoint
class CommonDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(uiConfig: IDialogUiConfig) = CommonDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_UI_CONFIG, uiConfig)
            }
        }
    }

    private val dialogEventBus by inject<EventBus>()

    private val viewModel by viewModel<DialogViewModel>()

    private val uiConfig by lazy { requireArguments().getParcelable<DialogUiConfig>(EXTRA_UI_CONFIG) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        observeEmptyEvent(viewModel.positiveButtonClickEvent) {
            dialogEventBus.post(PositiveButtonClickEvent(tag!!))
        }
        observeEmptyEvent(viewModel.negativeButtonClickEvent) {
            dialogEventBus.post(NegativeButtonClickEvent(tag!!))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FrameLayout(requireActivity()).also {
        DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.layout_dialog, it, true)
            ?.apply {
                lifecycleOwner = viewLifecycleOwner
                setVariable(BR.viewModel, viewModel)
                setVariable(BR.uiConfig, uiConfig)
            }
    }
}*/
