package com.example.massor

import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.clj.fastble.utils.HexUtil
import com.example.massor.adapter.DeviceAdapter
import com.example.massor.comm.ObserverManager
import com.example.massor.util.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() ,View.OnClickListener{

    internal var ruleConfig : BleScanRuleConfig? = null
    lateinit var bleManager:BleManager
    lateinit var adapter:DeviceAdapter
    lateinit var operatingAnim:Animation
    var lastTime :Long?=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initview()
        setConfig()
        checkBle()
        startScanCheck()
    }

    override fun onResume() {
        super.onResume()
        showConnectedDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.disconnectAllDevice()
        bleManager.destroy()
    }

    private fun checkBle() {
        bleManager = BleManager.getInstance()
        if (!bleManager.isSupportBle){
            Utils.showToast(this,"当前设备不支持蓝牙")
            finish()
        }
        if (!bleManager.isBlueEnable){
            bleManager.enableBluetooth()
        }

        val mac = intent.getStringExtra("mac")
        if (mac != null){
            ruleConfig = BleScanRuleConfig.Builder()
                    .setDeviceMac(mac)
                    .setAutoConnect(true)
                    .build()
            bleManager.initScanRule(ruleConfig)
        }
    }

    private fun startScanCheck() {
        if (!bleManager.isBlueEnable) {
            bleManager.enableBluetooth()
            //给点蓝牙初始化的时间
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    if (bleManager.isBlueEnable) {
                        startScan()
                    }
                }
            }
            timer.schedule(task, 500)
        }else{
            startScan()
        }
    }

    private fun startScan() {
        bleManager.scan(object:BleScanCallback(){
            override fun onScanFinished(p0: MutableList<BleDevice>?) {
                img_loading.clearAnimation()
                img_loading.visibility = View.INVISIBLE
                btn_scan.text = getString(R.string.start_scan)
            }
            override fun onScanStarted(p0: Boolean) {
                adapter.clearScanDevice()
                adapter.notifyDataSetChanged()
                img_loading.startAnimation(operatingAnim)
                img_loading.visibility = View.VISIBLE
                btn_scan.text = getString(R.string.stop_scan)
            }
            override fun onScanning(bleDevice: BleDevice?) {
                adapter.addDevice(bleDevice!!)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initview() {
        setSupportActionBar(toolbar)
        btn_scan.setOnClickListener(this)
        btn_setting.setOnClickListener(this)
        adapter = DeviceAdapter(this)
        list_device.adapter = adapter
        operatingAnim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate)
        operatingAnim.setInterpolator(LinearInterpolator())

        adapter.setOnDeviceClickListener(object: DeviceAdapter.OnDeviceClickListener {
            override fun onConnect(bleDevice: BleDevice?) {
                if (!bleManager.isConnected(bleDevice)) {
                    bleManager.cancelScan()
                    connect(bleDevice!!)
                    /*val intent = Intent(this@MainActivity,AdjustActivity::class.java)
                    intent.putExtra(AdjustActivity.KEY_DATA, bleDevice)
                    startActivity(intent)*/
                }
            }

            override fun onDisConnect(bleDevice: BleDevice?) {
                if (bleManager.isConnected(bleDevice)) {
                    bleManager.disconnect(bleDevice)
                }
            }

            override fun onDetail(bleDevice: BleDevice?) {
                if (bleManager.isConnected(bleDevice)) {
                    val intent = Intent(this@MainActivity,AdjustActivity::class.java)
                    intent.putExtra(AdjustActivity.KEY_DATA, bleDevice)
                    startActivity(intent)
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when(v){
           btn_scan ->{
                if (btn_scan.text == getString(R.string.start_scan)) {
                    startScanCheck()
                } else if (btn_scan.text == getString(R.string.stop_scan)) {
                    bleManager.cancelScan()
                }
            }
            btn_setting ->{
                if (layout_setting.visibility == View.VISIBLE) {
                    layout_setting.visibility = View.GONE
                    btn_setting.text = getString(R.string.expand_search_settings)
                } else {
                    layout_setting.visibility = View.VISIBLE
                    btn_setting.text = getString(R.string.retrieve_search_settings)
                }
            }

        }
    }

    private fun showConnectedDevice(){
        val deviceList = BleManager.getInstance().allConnectedDevice
        adapter.clearConnectedDevice()
        for (bleDevice in deviceList) {
            adapter.addDevice(bleDevice)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setConfig() {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1,5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000)
    }

    private fun connect(bleDevice: BleDevice) {
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {
                //progressDialog.show()
                Utils.showDiaLog(this@MainActivity,getString(R.string.connecting))
            }

            override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                img_loading.clearAnimation()
                img_loading.visibility = View.INVISIBLE
                btn_scan.text = getString(R.string.start_scan)
                //progressDialog.dismiss()
                Utils.dismissDialog()
                Utils.showToast(this@MainActivity, getString(R.string.connect_fail))
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                //progressDialog.dismiss()
                Utils.dismissDialog()
                adapter.addDevice(bleDevice)
                adapter.notifyDataSetChanged()
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                //progressDialog.dismiss()
                Utils.dismissDialog()

                adapter.removeDevice(bleDevice)
                adapter.notifyDataSetChanged()

                if (isActiveDisConnected) {
                    Utils.showToast(this@MainActivity, getString(R.string.active_disconnected))
                } else {
                    Utils.showToast(this@MainActivity, getString(R.string.disconnected))
                    ObserverManager.getInstance().notifyObserver(bleDevice)
                }

            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis()-lastTime!!)>2000){
                Utils.showToast(this,"再按一次退出")
                lastTime = System.currentTimeMillis()
            }else{
                finish()
                System.exit(0)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}
