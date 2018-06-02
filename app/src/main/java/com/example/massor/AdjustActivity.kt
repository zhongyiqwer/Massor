package com.example.massor

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_adjust.*

/**
 * Created by Administrator on 2018/6/2.
 */
class AdjustActivity :AppCompatActivity(){

    companion object {
        val KEY_DATA = "key_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjust)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar2)
        toolbar2.title = "Mosser"
        toolbar2.setTitleTextColor(Color.WHITE)
    }
}