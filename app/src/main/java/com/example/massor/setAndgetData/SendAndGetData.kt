package com.example.massor.setAndgetData

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import java.util.*

/**
 * Created by ZY on 2018/6/21.
 */
class SendAndGetData {

    lateinit var bleDevice: BleDevice
    lateinit var writeGattCharacteristic: BluetoothGattCharacteristic
    lateinit var readGattCharacteristic: BluetoothGattCharacteristic
    lateinit var dataCallback: DataCallback

    val writeUUID = "0000ffe9-0000-1000-8000-00805f9b34fb"
    val readUUID = "0000ffe4-0000-1000-8000-00805f9b34fb"

    fun setCallback(callback: DataCallback){
        this.dataCallback = callback
    }

    fun conn(bleDevice: BleDevice){
        this.bleDevice = bleDevice
        showData()
    }

    private fun showData() {
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
                    //ble_charid.text = writeGattCharacteristic.uuid.toString() + " " + property.toString()
                    writeFlag = true
                }
                if (gattCharacteristic.uuid.toString().equals("0000ffe4-0000-1000-8000-00805f9b34fb")) {
                    readGattCharacteristic = gattCharacteristic
                    println("read=" + readGattCharacteristic.uuid.toString())
                    //ble_service.text = readGattCharacteristic.uuid.toString() + " " + property.toString()
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
                //startWrite()
                break
            }
        }

        /*startRead()
    startWrite()*/
    }

    fun startWrite(byte: ByteArray) {
        if (writeGattCharacteristic!=null){
            BleManager.getInstance().write(
                    bleDevice,
                    writeGattCharacteristic.service.uuid.toString(),
                    writeGattCharacteristic.uuid.toString(),
                    //HexUtil.hexStringToBytes(string),
                    byte,
                    object : BleWriteCallback() {
                        override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                            println("write success, current: " + current
                                    + " total: " + total
                                    + " justWrite: " + HexUtil.formatHexString(justWrite))
                            dataCallback.writeCallback(justWrite)
                        }

                        override fun onWriteFailure(p0: BleException?) {
                            println(p0.toString())
                        }

                    }
            )
        }
    }

    private fun starNotify() {
        BleManager.getInstance().notify(
                bleDevice, readGattCharacteristic.service.uuid.toString(),
                readGattCharacteristic.uuid.toString(),
                object : BleNotifyCallback() {
                    override fun onCharacteristicChanged(notify: ByteArray?) {
                        //一次通知结束符 0d0a
                        println("notify success: " + HexUtil.formatHexString(notify))
                        dataCallback.notifyCallback(notify)
                    }

                    override fun onNotifyFailure(p0: BleException?) {
                        println(p0.toString())
                    }

                    override fun onNotifySuccess() {
                        println("通知成功")
                    }
                }
        )
    }
}