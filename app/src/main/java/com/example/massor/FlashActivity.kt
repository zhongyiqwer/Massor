package com.example.massor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.example.massor.util.Utils
import java.util.*

/**
 * Created by ZY on 2018/5/31.
 */
class FlashActivity :AppCompatActivity(){

    internal var intent : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_flash)
        intent = Intent(this, MainActivity::class.java)
        val timer = Timer()
        val task = object : TimerTask(){
            override fun run() {
                getMac()
                checkMyPremisson()
            }

        }
        timer.schedule(task,1500)

    }

    private fun checkMyPremisson() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
        }else{
            startActivity(intent)
            finish()
        }
    }

    private fun getMac() {
        val preferences = getSharedPreferences("config", 0)
        val mac = preferences.getString("mac", null)
        intent!!.putExtra("mac",mac)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(intent)
                finish()
            }else{
                Utils.showToast(this,"不开启位置权限可能导致蓝牙不可用")
                finish()
            }
        }
    }
}