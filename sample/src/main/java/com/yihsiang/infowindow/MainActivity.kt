package com.yihsiang.infowindow

import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import com.yihsiang.infowindow.databinding.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val infoWindows = mutableListOf<InfoWindow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.content, null, false)
            infoWindows += InfoWindow(v, Gravity.TOP).apply { show(it, 10f) }
        }
        binding.btn2.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.content, null, false)
            infoWindows += InfoWindow(v, Gravity.BOTTOM).apply { show(it) }
        }

        binding.btn3.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.content, null, false)
            infoWindows += InfoWindow(v, Gravity.TOP).apply { show(it) }
        }
        binding.btn4.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.content, null, false)
                .apply {
                    findViewById<TextView>(R.id.tv).text = "Test"
                }
            infoWindows += InfoWindow(v, Gravity.TOP).apply { show(it) }
        }
        binding.btn5.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.content, null, false)
                .apply {
                    findViewById<TextView>(R.id.tv).text = "Test".repeat(15)
                }
            infoWindows += InfoWindow(v, Gravity.BOTTOM).apply { show(it) }
        }
        binding.next.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        infoWindows.forEach { it.dismiss() }
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
    }
}