package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicReference

class DistinctChildLiveData<T>(private val parentLiveData: LiveData<T>) : LiveData<T>() {
    private val oldData = AtomicReference<T>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        parentLiveData.observe(owner) { newData: T ->
            if (oldData.get() != newData) {
                oldData.set(newData)
                value = newData
            }
            super.observe(owner, observer)
        }
    }
}
