package com.example.massor

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleDevice
import com.example.massor.comm.Observer
import com.example.massor.comm.ObserverManager
import kotlinx.android.synthetic.main.activity_adjust.*
import kotlinx.android.synthetic.main.activity_adjust2.*


/**
 * Created by Administrator on 2018/6/2.
 */
class AdjustActivity2 :AppCompatActivity(),Observer,View.OnClickListener{

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
        btn_pulse_stop.setOnClickListener(this)
        btn_pulse_pause.setOnClickListener(this)
        btn_pulse_resume.setOnClickListener(this)
        btn_set_pulse_PWR.setOnClickListener(this)
        btn_get_pulse_PWR.setOnClickListener(this)
        add_pulse_PWR.setOnClickListener(this)
        sub_pulse_PWR.setOnClickListener(this)

        val arrayList = ArrayList<String>()

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, arrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setOnItemClickListener { parent, view, position, id ->

        }
    }

    override fun onClick(v: View?) {

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

