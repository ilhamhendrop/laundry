package com.greensoft.laundryapp.ui.fragment.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.ui.activity.history.HistoryBarangActivity
import com.greensoft.laundryapp.ui.activity.history.HistoryPembayaranActivity
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "History"

        cvBarang.setOnClickListener {
            val intent = Intent(context, HistoryBarangActivity::class.java)
            startActivity(intent)
        }

        cvPembayaran.setOnClickListener {
            val intent = Intent(context, HistoryPembayaranActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

}