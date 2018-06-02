package com.example.massor.dialog

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleDevice
import com.example.massor.R
import com.example.massor.adapter.DeviceAdapter
import kotlinx.android.synthetic.main.dialog_prograse.view.*

/**
 * Created by ZY on 2018/6/1.
 */
class progreseDialog {

    var context :Context
    lateinit var builder :AlertDialog
    lateinit var view :View

    constructor(context: Context){
        this.context = context
        buildDialog()
    }

    private fun buildDialog() {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_prograse, null)
        builder = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()
    }

    fun show(msg:String){
        view.prograse_tv.text = msg
        builder.show()
    }

    fun dismiss(){
        builder.dismiss()
    }

}