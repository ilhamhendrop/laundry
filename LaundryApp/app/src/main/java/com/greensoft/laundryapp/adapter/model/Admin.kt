package com.greensoft.laundryapp.adapter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Admin (var id: String?,
                  var name: String?,
                  var alamat: String?,
                  var phone: String?,
                  var foto: String?)
    : Parcelable {
        constructor() : this("", "", "", "", "")
    }