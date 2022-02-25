package com.yihsiang.infowindow

import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import com.yihsiang.infowindow.databinding.*

class TestInfoWindowFragment : Fragment() {

    private lateinit var binding: FragmentInfoWindowBinding

    private var infoWindow: InfoWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btn.setOnClickListener {
            val v = LayoutInflater.from(requireContext()).inflate(R.layout.content, null, false)
                .apply {
                    findViewById<TextView>(R.id.tv).text = "Test".repeat(10)
                }
            infoWindow = InfoWindow(v, Gravity.BOTTOM).apply { show(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        infoWindow?.dismiss()
    }
}