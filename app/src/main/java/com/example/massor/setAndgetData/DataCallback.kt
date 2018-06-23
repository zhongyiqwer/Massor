package com.example.massor.setAndgetData

/**
 * Created by ZY on 2018/6/22.
 */
interface DataCallback {
    fun writeCallback(byteArray: ByteArray?)

    fun notifyCallback(byteArray: ByteArray?)
}