package com.doctor.eprescription.features.homescreen.display

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.doctor.eprescription.R
import com.doctor.eprescription.basefragment.BaseFragmentVB
import com.doctor.eprescription.constants.ArgsConstant
import com.doctor.eprescription.databinding.FragmentHomeBinding
import com.doctor.eprescription.domain.model.ErrorModel
import com.doctor.eprescription.entrypoint.adapter.DrawerAdapter
import com.doctor.eprescription.extension.collectWhenStarted
import com.doctor.eprescription.extension.errorDialog
import com.doctor.eprescription.extension.safeShow
import com.doctor.eprescription.features.homescreen.display.adapter.HomeListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : BaseFragmentVB<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by viewModels<HomeViewModel>()

    private val mDrawerAdapter by lazy { DrawerAdapter() }
    private var ed: androidx.appcompat.app.AlertDialog? = null

    private val mAdapter by lazy {
        HomeListAdapter(
            onItemClick = {
                viewModel.onPatientClick(it)
                /*collectWhenStarted {
                    ed = errorDialog(ErrorModel("Error", "Message")) {
                        ed?.dismiss()
                    }
                    ed?.safeShow()
                    ed?.safeShow()
                }*/
            },
            onEditClick = { viewModel.onEditPatientClick(it) },
            onDeleteClick = { viewModel.onDeletePatientClick(it) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.content.apply {
            rvPatients.layoutManager = LinearLayoutManager(requireContext())
            rvPatients.adapter = mAdapter

            ivToggleDrawer.setOnClickListener {
                viewModel.openDrawer()
            }

            tvAddPatient.setOnClickListener {
                viewModel.onAddPatientClick()
            }

            etSearch.doOnTextChanged { text, start, before, count -> viewModel.onSearch(text.toString()) }
        }

        addDrawerListener()
        setUpDrawerList()
        addObservers()
        setFragmentResultListener("refresh") { _, bundle ->
            val refresh = bundle.getBoolean("refresh")
            if (refresh) {
                viewModel.refreshPatientList()
            }
        }
    }


    private fun setUpDrawerList() {
        binding.compDrawer.apply {
            rvDrawer.layoutManager = LinearLayoutManager(requireContext())
            rvDrawer.adapter = mDrawerAdapter
        }
    }

    private fun addDrawerListener() {
        binding.navDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                viewModel.openDrawer()
            }

            override fun onDrawerClosed(drawerView: View) {
                viewModel.closeDrawer()
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })
    }

    override fun getMyViewModel() = viewModel

    private fun addObservers() {
        collectWhenStarted {
            viewModel.drawerItems.collectLatest {
                mDrawerAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.showDrawer.collectLatest {
                if (it) {
                    binding.navDrawer.openDrawer(GravityCompat.START)
                } else {
                    binding.navDrawer.close()
                }
            }
        }

        collectWhenStarted {
            viewModel.loginResponse.collectLatest {
                binding.compDrawer.tvName.text = it?.data?.fullName
                Glide.with(requireContext())
                    .load(it?.data?.profilePicture)
                    .placeholder(R.drawable.male_placeholder)
                    .into(binding.compDrawer.ivDoctor)
            }
        }

        collectWhenStarted {
            viewModel.patients.collectLatest {
                mAdapter.submitList(it)
            }
        }

        collectWhenStarted {
            viewModel.channel.collectLatest { event ->
                when (event) {
                    is HomeViewModel.NavigationEvents.NavigateToAddPatient -> {
                        findNavController()
                            .navigate(
                                HomeFragmentDirections.actionHomeFragmentToAddEditPatientFragment(
                                    event.patient
                                )
                            )
                    }

                    is HomeViewModel.NavigationEvents.NavigateToPatientMenu -> {
                        val bundle = Bundle().apply {
                            putParcelable(ArgsConstant.PATIENT, event.patient)
                            putParcelable(ArgsConstant.LOGIN_RESPONSE, event.loginResponse)
                        }
                        findNavController().navigate(R.id.patient_menu_graph, bundle)
                    }
                }
            }
        }
    }

}