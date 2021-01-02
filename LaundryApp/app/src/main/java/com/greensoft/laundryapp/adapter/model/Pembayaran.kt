package com.greensoft.laundryapp.adapter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pembayaran (var id: String?,
                       var idBarang: String?,
                       var idPelanggan: String?,
                       var tipe: String?,
                       var kali: Int?,
                       var total: Int?,
                       var hari: String?,
                       var proses: String?)
    : Parcelable {
        constructor(): this("", "", "","", 0, 0,"", "")
    }