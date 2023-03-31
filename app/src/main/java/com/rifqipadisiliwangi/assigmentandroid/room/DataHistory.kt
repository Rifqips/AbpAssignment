package com.rifqipadisiliwangi.assigmentandroid.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class DataHistory(
    @PrimaryKey
    val hasil : String,
    @ColumnInfo(name = "valuesatu")
    val valueSatu: String,
    @ColumnInfo(name = "valuedua")
    val valueDua: String,
    @ColumnInfo(name = "metode")
    val metode: String,
) : Parcelable
