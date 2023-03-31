package com.rifqipadisiliwangi.assigmentandroid.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityHomeBinding
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
import com.rifqipadisiliwangi.assigmentandroid.view.adapter.HistoryAdapter
import com.rifqipadisiliwangi.assigmentandroid.view.kalkulator.KalkulatorActivity
import com.rifqipadisiliwangi.assigmentandroid.viewmodel.HistoryViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding

    var historyDB : DatabaseHistory? = null
    lateinit var adapterHistory : HistoryAdapter
    lateinit var viewModel: HistoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        historyVm()
        historyDB = DatabaseHistory.getInstance(this)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.getAllHistoryObserve().observe(this,{
            adapterHistory.setHistoryData(it as ArrayList<DataHistory>)
        })

        binding.btnKalkulator.setOnClickListener {
            startActivity(Intent(this, KalkulatorActivity::class.java))
        }
    }

    fun historyVm(){
        adapterHistory = HistoryAdapter(ArrayList())
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHistory.adapter = adapterHistory
    }

    fun getAllHistory(){
        GlobalScope.launch {
            var data = historyDB?.HistoryItem()?.getHistory()
            run{
                adapterHistory = HistoryAdapter(data!!)
                binding.rvHistory.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
                binding.rvHistory.adapter = adapterHistory
            }
        }
    }
}