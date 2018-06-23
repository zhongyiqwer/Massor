package com.example.massor

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleDevice
import com.clj.fastble.utils.HexUtil
import com.example.massor.comm.Observer
import com.example.massor.comm.ObserverManager
import com.example.massor.setAndgetData.DataCallback
import com.example.massor.setAndgetData.SendAndGetData
import com.example.massor.util.Protocol
import com.example.massor.util.Utils
import kotlinx.android.synthetic.main.activity_adjust2.*


/**
 * Created by Administrator on 2018/6/2.
 */
class AdjustActivity2 :AppCompatActivity(),Observer,View.OnClickListener,AdapterView.OnItemSelectedListener,DataCallback{

    companion object {
        val KEY_DATA = "key_data"
    }

    lateinit var bleDevice: BleDevice
    lateinit var sendAndGetData: SendAndGetData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjust2)
        initView()
        initData()
        sendAndGetData = SendAndGetData()
        sendAndGetData.setCallback(this)
        sendAndGetData.conn(bleDevice)
        ObserverManager.getInstance().addObserver(this)
    }

    fun initData() {
        bleDevice = intent.getParcelableExtra(KEY_DATA)
        if (bleDevice == null)
            finish()
    }

    fun initView() {
        toolbar3.title = "Mosser"
        toolbar3.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar3)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        btn_set_zhen.setOnClickListener(this)
        btn_get_zhen.setOnClickListener(this)
        btn_del_zhen.setOnClickListener(this)
        btn_set_loop.setOnClickListener(this)
        btn_get_loop.setOnClickListener(this)
        btn_pulse_run.setOnClickListener(this)
        btn_pulse_pause.setOnClickListener(this)
        btn_get_pulse_PWR.setOnClickListener(this)
        add_pulse_PWR.setOnClickListener(this)
        sub_pulse_PWR.setOnClickListener(this)

        val arrayList = ArrayList<String>()
        for (i in 0..13){
            arrayList.add(""+i)
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinner.setSelection(position)
    }

    override fun onClick(v: View?) {
        when(v){
            btn_set_zhen->{
                val position = spinner.selectedItemPosition
                if (position >=7){
                    Utils.showToast(this,"序号不能小于6")
                }else{
                    //{25, 1, 0, 75, 20, 320, 80, 128, { 150, 2500,  75}, {  75,  75,  75,  75} }
                    val write = edit_write.text.toString()
                    println("write="+write)
                    val list = write.split(" ")
                    if (write != null && list.size==15){
                        val map = HashMap<String, String>()
                        map["frameIndex"] = position.toString()
                        map["frameParam"] = write
                        val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_SET_FRAME, map)
                        sendAndGetData.startWrite(byteArr)
                    }else{
                        Utils.showToast(this,"输入数据错误")
                    }
                }
            }
            btn_get_zhen->{
                val position = spinner.selectedItemPosition
                val map = HashMap<String, String>()
                map["frameIndex"] = position.toString()
                val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_GET_FRAME, map)
                sendAndGetData.startWrite(byteArr)
            }
            btn_del_zhen->{
                val position = spinner.selectedItemPosition
                if (position >=7){
                    Utils.showToast(this,"序号不能小于6")
                }else{
                    val map = HashMap<String, String>()
                    map["frameIndex"] = position.toString()
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_DEL_FRAME, map)
                    sendAndGetData.startWrite(byteArr)
                }
            }
            btn_set_loop->{
                //12 0 12 8
                val write = edit_write.text.toString()
                val split = write.split(" ")
                if (write == null || split.size!=15){
                    Utils.showToast(this,"输入数据错误")
                }else{
                    val map = HashMap<String, String>()
                    map["loopParam"] = write
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_SET_LOOP, map)
                    sendAndGetData.startWrite(byteArr)
                }
            }
            btn_get_loop->{
                val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_GET_LOOP, null)
                sendAndGetData.startWrite(byteArr)
            }
            btn_pulse_run->{
                if (btn_pulse_run.text.equals("运行")){
                    btn_pulse_run.text = "关闭"
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_RUN_PULSE, null)
                    sendAndGetData.startWrite(byteArr)
                }else if (btn_pulse_run.text.equals("关闭")){
                    btn_pulse_run.text = "运行"
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_STOP_PULSE, null)
                    sendAndGetData.startWrite(byteArr)
                }

            }
            btn_pulse_pause->{
                if (btn_pulse_pause.text.equals("暂停")){
                    btn_pulse_pause.text = "恢复"
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_PAUSE_PULSE, null)
                    sendAndGetData.startWrite(byteArr)
                }else if (btn_pulse_pause.text.equals("恢复")){
                    btn_pulse_pause.text = "暂停"
                    val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_RESUME_PULSE, null)
                    sendAndGetData.startWrite(byteArr)
                }
            }
            btn_get_pulse_PWR->{
                val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_GET_PULSEPWR, null)
                sendAndGetData.startWrite(byteArr)
            }
            add_pulse_PWR->{
                val pulsePWR = tv_pulse_PWR.text.toString().toInt()
                if (pulsePWR<99){
                    tv_pulse_PWR.text = (pulsePWR+1).toString()
                }
                val map = HashMap<String, String>()
                map["pulsePWR"] = (pulsePWR+1).toString()
                val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_SET_PULSEPWR, map)
                sendAndGetData.startWrite(byteArr)
            }
            sub_pulse_PWR->{
                val pulsePWR = tv_pulse_PWR.text.toString().toInt()
                if (pulsePWR>0){
                    tv_pulse_PWR.text = (pulsePWR-1).toString()
                }
                val map = HashMap<String, String>()
                map["pulsePWR"] = (pulsePWR+1).toString()
                val byteArr = Protocol.getSendCmdByteArr(Protocol.CMD_SET_PULSEPWR, map)
                sendAndGetData.startWrite(byteArr)
            }

        }
    }

    /*
      发送数据和接收数据
     */
    var count:Int =0
    lateinit var byteArray :ByteArray

    override fun writeCallback(byteArray: ByteArray?) {
        println("writeCallback="+HexUtil.formatHexString(byteArray))
    }

    override fun notifyCallback(notify: ByteArray?) {
        if (notify!=null){
            if (notify[0] == 0xEB.toByte() && notify[1] == 0x90.toByte() &&
                    notify[2] == 0xEB.toByte() && notify[3] == 0x90.toByte()){
                println("进入重置")
                count=0
                byteArray = ByteArray(64)
            }

            for (i in notify.indices){
                if (notify[i]==0x0D.toByte() && notify[i+1] == 0x0A.toByte() && count == 64){
                    count = 0
                    println("接收的拼接结果："+HexUtil.formatHexString(byteArray))
                    val data = Protocol.getReceiveCmdData(byteArray)
                    tv_read.text = data

                }else {
                    byteArray[count] = notify[i]
                    count++
                }
            }
        }
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

