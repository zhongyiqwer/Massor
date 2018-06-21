package com.example.massor

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.example.massor.comm.Observer
import com.example.massor.comm.ObserverManager
import com.example.massor.util.Utils
import kotlinx.android.synthetic.main.activity_adjust.*
import kotlinx.android.synthetic.main.activity_adjust_contier.*


/**
 * Created by Administrator on 2018/6/2.
 */
class AdjustActivity :AppCompatActivity(),Observer {

    companion object {
        val KEY_DATA = "key_data"
    }

    lateinit var bleDevice: BleDevice
    lateinit var writeGattCharacteristic: BluetoothGattCharacteristic
    lateinit var readGattCharacteristic: BluetoothGattCharacteristic

    val writeUUID = "0000ffe9-0000-1000-8000-00805f9b34fb"
    val readUUID = "0000ffe4-0000-1000-8000-00805f9b34fb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjust)
        initView()
        initData()
        showData()
        ObserverManager.getInstance().addObserver(this)
    }

    fun initData() {
        bleDevice = intent.getParcelableExtra(KEY_DATA)
        if (bleDevice == null)
            finish()
        ble_name.text = bleDevice.name
        ble_mac.text = bleDevice.mac
    }

    fun initView() {
        toolbar2.title = "Mosser"
        toolbar2.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun startRead() {

        BleManager.getInstance().read(bleDevice,
                readGattCharacteristic.service.uuid.toString(),
                readGattCharacteristic.uuid.toString(),
                object : BleReadCallback() {
                    override fun onReadSuccess(p0: ByteArray?) {
                        println("write success: " + String(p0!!))
                        runOnUiThread {
                            //tv_read.text = HexUtil.formatHexString(p0)
                            tv_read.text = String(p0!!)
                            //println("write success: "+String(p0!!))
                        }
                    }

                    override fun onReadFailure(p0: BleException?) {
                        println(p0.toString())
                        runOnUiThread {
                            Utils.showToast(this@AdjustActivity, "读取失败")
                            //println(p0.toString())
                        }
                    }

                })
    }

    private fun starNotify() {
        BleManager.getInstance().notify(
                bleDevice, readGattCharacteristic.service.uuid.toString(),
                readGattCharacteristic.uuid.toString(),
                object : BleNotifyCallback() {
                    override fun onCharacteristicChanged(p0: ByteArray?) {
                        runOnUiThread {
                            //tv_read.text = HexUtil.formatHexString(p0)

                            tv_read.text = String(p0!!)
                            println("write success: " + String(p0!!))
                        }
                    }

                    override fun onNotifyFailure(p0: BleException?) {
                        runOnUiThread {
                            Utils.showToast(this@AdjustActivity, "通知失败")
                            println(p0.toString())
                        }
                    }

                    override fun onNotifySuccess() {
                        runOnUiThread {
                            println("通知成功")
                        }
                    }

                }
        )
    }

    private fun startWrite() {
        btn_sendWrite.setOnClickListener {
            //val intent = Intent(this, ::class.java)

            val string = ed_write.text.toString()
            val byte = string.toByteArray()
            if (TextUtils.isEmpty(string)) {
                Utils.showToast(this, "输入为空")
            } else {
                println("writeString="+string)
                println("writebyteToString="+String(byte))

                BleManager.getInstance().write(
                        bleDevice,
                        writeGattCharacteristic.service.uuid.toString(),
                        writeGattCharacteristic.uuid.toString(),
                        //HexUtil.hexStringToBytes(string),
                        byte,
                        object : BleWriteCallback() {
                            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                                runOnUiThread {
                                    Utils.showToast(this@AdjustActivity, "发送成功")
                                    println("write success, current: " + current
                                            + " total: " + total
                                            + " justWrite: " + String(justWrite!!))
                                }
                            }

                            override fun onWriteFailure(p0: BleException?) {
                                runOnUiThread {
                                    Utils.showToast(this@AdjustActivity, "发送失败")
                                    println(p0.toString())
                                }
                            }

                        }
                )
            }
        }
    }

    fun showData() {
        var writeFlag = false
        var readFlag = false
        val gatt = BleManager.getInstance().getBluetoothGatt(bleDevice)
        val serviceList = ArrayList<BluetoothGattService>()
        for (service in gatt.services) {
            serviceList.add(service)
        }
        for (gattService in serviceList) {
            //val gattService = serviceList[0]

            val characteristicList = ArrayList<BluetoothGattCharacteristic>()
            for (characteristic in gattService.characteristics) {
                characteristicList.add(characteristic)
            }

            for (gattCharacteristic in characteristicList) {
                //val gattCharacteristic = characteristicList[0]
                val property = StringBuilder()
                val propList = ArrayList<Int>()
                val charaProp = gattCharacteristic.properties
                println("charaProp = " + charaProp)

                if (charaProp and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    property.append("Read")
                    property.append(" , ")
                    propList.add(BluetoothGattCharacteristic.PROPERTY_READ)
                }
                if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
                    property.append("Write")
                    property.append(" , ")
                    propList.add(BluetoothGattCharacteristic.PROPERTY_WRITE)
                }
                if (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                    property.append("Write No Response")
                    property.append(" , ")
                    propList.add(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
                }
                if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    property.append("Notify")
                    property.append(" , ")
                    propList.add(BluetoothGattCharacteristic.PROPERTY_NOTIFY)
                }
                if (charaProp and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                    property.append("Indicate")
                    property.append(" , ")
                    propList.add(BluetoothGattCharacteristic.PROPERTY_INDICATE)
                }
                println("property1 = " + property.toString())
                if (property.length > 1) {
                    property.delete(property.length - 2, property.length - 1)
                }
                println("property2 = " + property.toString())


                if (gattCharacteristic.uuid.toString().equals("0000ffe9-0000-1000-8000-00805f9b34fb")) {
                    writeGattCharacteristic = gattCharacteristic
                    println("write=" + writeGattCharacteristic.uuid.toString())
                    ble_charid.text = writeGattCharacteristic.uuid.toString() + " " + property.toString()
                    writeFlag = true
                }
                if (gattCharacteristic.uuid.toString().equals("0000ffe4-0000-1000-8000-00805f9b34fb")) {
                    readGattCharacteristic = gattCharacteristic
                    println("read=" + readGattCharacteristic.uuid.toString())
                    ble_service.text = readGattCharacteristic.uuid.toString() + " " + property.toString()
                    readFlag = true
                }

                /*if(propList.contains(BluetoothGattCharacteristic.PROPERTY_WRITE) && !writeFlag){
                writeGattCharacteristic = gattCharacteristic
                println("write="+writeGattCharacteristic.uuid.toString())
                writeFlag = true
            }
            if (propList.contains(BluetoothGattCharacteristic.PROPERTY_READ) && !readFlag){
                readGattCharacteristic = gattCharacteristic
                println("read="+readGattCharacteristic.uuid.toString())
                readFlag = true
            }*/

                if (writeFlag && readFlag) {
                    break
                }
            }

            if (writeFlag && readFlag) {
                //startRead()
                starNotify()
                startWrite()
                break
            }
        }

        /*startRead()
    startWrite()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().clearCharacterCallback(bleDevice)
        ObserverManager.getInstance().deleteObserver(this)
    }

    override fun disConnected(device: BleDevice) {
        if (device != null && device != null && device.key.equals(bleDevice.key))
            finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event)
    }
}

