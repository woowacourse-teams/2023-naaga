package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class DisposableLiveData<T>(private val parentLiveData: LiveData<T>) : LiveData<T>() {
    private var isFirst = AtomicBoolean(true)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        parentLiveData.observe(owner) {
            setValue(it)
        }
        super.observe(owner) { t: T ->
            if (isFirst.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
}
