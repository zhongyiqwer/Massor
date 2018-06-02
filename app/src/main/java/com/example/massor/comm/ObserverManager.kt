package com.example.massor.comm

import com.clj.fastble.data.BleDevice
import java.util.ArrayList

/**
 * Created by Administrator on 2018/6/2.
 */
object ObserverManager :Observable{
    fun getInstance(): ObserverManager {
        return this
    }

    private val observers = ArrayList<Observer>()

    override fun addObserver(obj: Observer) {
        observers.add(obj)
    }

    override fun deleteObserver(obj: Observer) {
        val i = observers.indexOf(obj)
        if (i >= 0) {
            observers.remove(obj)
        }
    }

    override fun notifyObserver(bleDevice: BleDevice) {
        for (i in observers.indices) {
            val o = observers[i]
            o.disConnected(bleDevice)
        }
    }

}