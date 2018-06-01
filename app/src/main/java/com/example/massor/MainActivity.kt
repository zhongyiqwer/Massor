package com.example.massor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.scan.BleScanRuleConfig
import com.example.massor.util.Utils

class MainActivity : AppCompatActivity() {

    internal var ruleConfig : BleScanRuleConfig? = null
    lateinit var bleManager:BleManager
    internal var bleList :ArrayList<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bleManager = BleManager.getInstance()

        if (!bleManager.isSupportBle){
            Utils.showToast(this,"当前设备不支持蓝牙")
            finish()
        }
        if (!bleManager.isBlueEnable){
            bleManager.enableBluetooth()
        }

        Log.e("bleState"," "+bleManager.isBlueEnable)

        setConfig()
        val mac = intent.getStringExtra("mac")
        if (mac != null){
            ruleConfig = BleScanRuleConfig.Builder()
                    .setDeviceMac(mac)
                    .setAutoConnect(true)
                    .setScanTimeOut(10000)
                    .build()
        }else{
            ruleConfig = BleScanRuleConfig.Builder()
                    .setScanTimeOut(10000)
                    .build()
        }
        bleManager.initScanRule(ruleConfig)

        bleManager.scan(object:BleScanCallback(){
            override fun onScanFinished(p0: MutableList<BleDevice>?) {
                //Utils.dismissDialog()
            }

            override fun onScanStarted(p0: Boolean) {
                //Utils.showDiaLog(this@MainActivity,"正在扫描...")
            }

            override fun onScanning(p0: BleDevice?) {
                Log.e("bleDevice"," "+p0!!.name)
            }

        })
    }

    private fun setConfig() {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1,3000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000)
    }
}
