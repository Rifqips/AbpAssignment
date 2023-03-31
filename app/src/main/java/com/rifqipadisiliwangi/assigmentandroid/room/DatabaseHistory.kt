package com.rifqipadisiliwangi.assigmentandroid.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [DataHistory::class], version = 2)
abstract class DatabaseHistory: RoomDatabase() {
    abstract fun HistoryItem() : DaoHistory

    companion object{
        private var INSTANCE : DatabaseHistory? = null

        fun getInstance(context : Context): DatabaseHistory? {
            if (INSTANCE == null){
                synchronized(DatabaseHistory::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        DatabaseHistory::class.java,"kalkulator.db")
                        .build()

                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}