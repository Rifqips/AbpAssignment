package com.rifqipadisiliwangi.assigmentandroid.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    var email : String ?= "",
    var password : String ?= "",
    var url : String ?= "",
    var username : String ?= "",
) : Parcelable