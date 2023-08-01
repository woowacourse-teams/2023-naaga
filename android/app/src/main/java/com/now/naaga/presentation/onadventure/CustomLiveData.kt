package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.now.domain.model.Game
import com.now.domain.model.Place
import java.util.concurrent.atomic.AtomicBoolean

abstract class CustomLiveData<T, R> : MutableLiveData<T>() {
    protected var lastValue: R? = null
    protected var isDiff = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t: T ->
            if (isDiff.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
}

class DestinationLiveData : CustomLiveData<Game, Place>() {
    override fun setValue(value: Game) {
        if (lastValue != value.destination) {
            lastValue = value.destination
            isDiff.set(true)
        }
        super.setValue(value)
    }
}
