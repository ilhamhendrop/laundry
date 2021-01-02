package com.greensoft.laundryapp.adapter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barang (var id: String?,
                   var name: String?,
                   var keterangan: String?,
                   var kategori: String?,
                   var harga: Int?,
                   var photo: String?)
    : Parcelable {
        constructor(): this("", "", "", "", 0, "")
    }