package com.doctor.eprescription.di.viewmodelscoped

import com.doctor.eprescription.domain.repo.LoginRepo
import com.doctor.eprescription.data.remote.repo.LoginRepoImpl
import com.doctor.eprescription.data.remote.repo.PatientRepoImpl
import com.doctor.eprescription.domain.interactors.EmailValidationUseCase
import com.doctor.eprescription.domain.interactors.PasswordValidationUseCase
import com.doctor.eprescription.domain.repo.PatientRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object LoginViewModelScoped {

    @Provides
    @ViewModelScoped
    fun provideLoginRepository(
        loginRepo: LoginRepoImpl
    ): LoginRepo = loginRepo

    @Provides
    @ViewModelScoped
    fun providePatientRepository(
        patientRepo: PatientRepoImpl
    ): PatientRepo = patientRepo

    @Provides
    @ViewModelScoped
    fun provideEmailValidationUseCase(): EmailValidationUseCase = EmailValidationUseCase()

    @Provides
    @ViewModelScoped
    fun providePasswordValidationUseCase(): PasswordValidationUseCase = PasswordValidationUseCase()
}