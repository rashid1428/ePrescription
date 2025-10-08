package com.doctor.eprescription.data.cache

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PrefKeys {
    val DOCTOR_EMAIL = stringPreferencesKey("DOCTOR_EMAIL")
    val IS_LOGGED_IN = booleanPreferencesKey("IS_LOGGED_IN")
    val LOGIN_RESPONSE = stringPreferencesKey("LOGIN_RESPONSE")
}