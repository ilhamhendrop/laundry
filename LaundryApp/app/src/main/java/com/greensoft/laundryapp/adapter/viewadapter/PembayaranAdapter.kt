package com.greensoft.laundryapp.adapter.viewadapter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greensoft.laundryapp.R
import com.greensoft.laundryapp.adapter.model.Barang
import com.greensoft.laundryapp.adapter.model.Pembayaran
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_pembayaran.view.*

class PembayaranAdapter(val pembayaran: Pembayaran, val context: Context): Item<GroupieViewHolder>() {
    var item: Barang? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val holer = viewHolder.itemView

        holer.txtHarga.text = StringBuilder("Rp ${pembayaran.total}")

        val barang = FirebaseDatabase.getInstance().getReference("/barang/${pembayaran.idBarang}")
        barang.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                item = snapshot.getValue(Barang::class.java)

                if (item != null){
                    holer.txtName.text = item?.name

                    Picasso.get()
                        .load(item?.photo)
                        .error(R.drawable.ic_broken_image)
                        .into(holer.imgPembayaran)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("erro_pembayaran", error.message)
            }

        })

        holer.imgCancel.setOnClickListener {
            val buy = FirebaseDatabase.getInstance()
                .getReference("/pembayaran/${pembayaran.idPelanggan}/${pembayaran.idBarang}").child(pembayaran.id.toString())

            buy.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show()
                }

        }

    }

    override fun getLayout(): Int {
        return R.layout.list_pembayaran
    }
}