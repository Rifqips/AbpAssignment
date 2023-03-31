package com.rifqipadisiliwangi.assigmentandroid.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface DaoHistory {

    @Insert
    fun addHistory(DataHistoryItem: DataHistory):Long

    @Query("SELECT * FROM DataHistory")
    fun getHistory() : List<DataHistory>

    @Query("SELECT count(*) FROM DataHistory WHERE DataHistory.hasil = :hasil")
    fun checkHistory(hasil: Int) : Int

    @Delete
    fun deleteHistory(DataHistory: DataHistory) : Int
}