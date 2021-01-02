package com.greensoft.laundryapp.adapter.viewadapter

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_barang.view.*

class BarangAdapter(val barang: Barang, val context: Context) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val tipe = viewHolder.itemView
        tipe.txtNama.text = barang.name
        tipe.txtHarga.text = StringBuilder("Price: Rp${barang.harga.toString()}")

        Picasso.get()
            .load(barang.photo)
            .placeholder(R.drawable.ic_broken_image)
            .error(R.drawable.ic_broken_image)
            .into(tipe.imgBarang)
    }

    override fun getLayout(): Int {
        return R.layout.list_barang
    }
}