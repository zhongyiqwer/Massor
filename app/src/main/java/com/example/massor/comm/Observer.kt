package com.example.massor.comm

import com.clj.fastble.data.BleDevice

/**
 * Created by Administrator on 2018/6/2.
 */
interface Observer {
     fun disConnected(bleDevice: BleDevice)
}