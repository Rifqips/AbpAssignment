package com.rifqipadisiliwangi.assigmentandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app)  {

    lateinit var allHistory : MutableLiveData<List<DataHistory>>

    init {
        allHistory = MutableLiveData()
        getAllHistory()
    }

    fun getAllHistoryObserve(): MutableLiveData<List<DataHistory>>{
        return allHistory
    }

    fun getAllHistory(){
        GlobalScope.launch {
            var historyDao = DatabaseHistory.getInstance(getApplication())!!.HistoryItem()
            var listHistory = historyDao.getHistory()
            allHistory.postValue(listHistory)
        }
    }
}