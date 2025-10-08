package com.doctor.eprescription.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class StateFlowDelegate<T>(
    stateFlow: Flow<T>,
    viewModelScope: CoroutineScope
) : ReadOnlyProperty<Any, T> {
    private var value: T? = null

    init {
        stateFlow
            .onEach { value = it }
            .launchIn(viewModelScope)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException("StateFlow value is not initialized yet.")
    }
}