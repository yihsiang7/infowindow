package com.yihsiang.infowindow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val t = supportFragmentManager.beginTransaction()
        t.add(R.id.container, TestInfoWindowFragment())
        t.commit()
    }
}