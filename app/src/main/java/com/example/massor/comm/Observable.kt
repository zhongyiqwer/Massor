package com.example.massor.comm

import com.clj.fastble.data.BleDevice

/**
 * Created by Administrator on 2018/6/2.
 */
interface Observable {
    fun addObserver(obj: Observer)

    fun deleteObserver(obj: Observer)

    fun notifyObserver(bleDevice: BleDevice)
}