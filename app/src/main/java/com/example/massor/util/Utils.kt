package com.example.massor.util

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.clj.fastble.BleManager
import com.example.massor.R
import com.example.massor.dialog.progreseDialog
import android.R.attr.data
import kotlin.experimental.and


/**
 * Created by ZY on 2018/5/31.
 */
object Utils {
    internal var diaLog : progreseDialog?=null

    fun showToast(context:Context,text:String){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }

    fun showDiaLog(context: Context,msg:String){
        if (diaLog == null) {
            diaLog = progreseDialog(context)
            diaLog!!.show(msg)
        } else {
            diaLog!!.show(msg)
        }
    }

    fun dismissDialog(){
        diaLog!!.dismiss()
    }

    private fun makeCheckSum(data :String):String{
        var dSum = 0
        var length = data.length
        var index = 0
        // 遍历十六进制，并计算总和
        while (index < length) {
            val s = data.substring(index, index + 2) // 截取2位字符
            dSum += Integer.parseInt(s, 16) // 十六进制转成十进制 , 并计算十进制的总和
            index = index + 2
        }

        val mod = dSum % 256 // 用256取余，十六进制最大是FF，FF的十进制是255
        var checkSumHex = Integer.toHexString(mod) // 余数转成十六进制
        length = checkSumHex.length
        if (length < 2) {
            checkSumHex = "0" + checkSumHex  // 校验位不足两位的，在前面补0
        }
        return checkSumHex
    }

}