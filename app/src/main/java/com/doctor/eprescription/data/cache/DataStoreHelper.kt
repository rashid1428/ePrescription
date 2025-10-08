package com.doctor.eprescription.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreHelper(private val prefDataStore: DataStore<Preferences>) {

    /*suspend fun <T> Preferences.Key<T>.setValue(value: T) {
        prefDataStore.edit { preferences -> preferences[this] = value }
    }*/

    suspend fun setValue(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        prefDataStore.edit { preferences -> preferences[prefKey] = value }
    }

    suspend fun <T> Preferences.Key<T>.getValue(defaultValue: T): T {
        return prefDataStore.data
            .catchAndHandleError()
            .map { preferences -> preferences[this] }
            .firstOrNull() ?: defaultValue
    }

    fun getValueAsFlow(key: String): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return prefDataStore.data
            .catchAndHandleError()
            .map { preferences ->
                preferences[prefKey] ?: ""
            }
    }

    fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return prefDataStore.data
            .catchAndHandleError()
            .map { prefs -> prefs[key] ?: defaultValue }
    }

    fun <T> getValueOrNull(key: Preferences.Key<T>): Flow<T?> {
        return prefDataStore.data
            .catchAndHandleError()
            .map { prefs -> prefs[key] }
    }

    suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        prefDataStore.edit { pref -> pref[key] = value }
    }

    private fun Flow<Preferences>.catchAndHandleError(): Flow<Preferences> {
        this.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        return this@catchAndHandleError
    }
}