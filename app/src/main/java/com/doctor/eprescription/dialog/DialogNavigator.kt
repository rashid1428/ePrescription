package com.doctor.eprescription.dialog

import androidx.fragment.app.FragmentManager

class DialogNavigator(private val fragmentManager: FragmentManager) {
    fun showDialog(tag: String, uiConfig: IDialogUiConfig) {
        if (fragmentManager.isFragmentNotExist(tag)) {
//            CommonDialogFragment.newInstance(uiConfig).show(fragmentManager, tag)
        }
    }
    
    fun hideDialog(tag: String) {
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
    }
    
    private fun FragmentManager.isFragmentNotExist(tag: String) = findFragmentByTag(tag) == null
}