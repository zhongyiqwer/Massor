package com.example.massor.util

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.example.massor.R
import com.example.massor.dialog.progreseDialog

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
}