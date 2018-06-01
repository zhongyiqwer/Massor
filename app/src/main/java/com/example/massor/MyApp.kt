package com.example.massor

import android.app.Application
import com.clj.fastble.BleManager

/**
 * Created by ZY on 2018/5/31.
 */
class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()
        BleManager.getInstance().init(this)
    }
}