package com.rifqipadisiliwangi.assigmentandroid.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityHomeBinding
import com.rifqipadisiliwangi.assigmentandroid.helper.SwipeToDeleteCallback
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.adapter.HistoryAdapter
import com.rifqipadisiliwangi.assigmentandroid.view.kalkulator.KalkulatorActivity
import com.rifqipadisiliwangi.assigmentandroid.viewmodel.HistoryViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

private const val TAG = "HomeActivity"
class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding

    var historyDB : DatabaseHistory? = null
    lateinit var adapterHistory : HistoryAdapter
    lateinit var viewModel: HistoryViewModel

    private lateinit var preferences: PreferencesClass
    private lateinit var mFirebaseInstance: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        historyVm()
        swipeDelete()

        preferences = PreferencesClass(this)
        mFirebaseInstance = FirebaseDatabase.getInstance().reference

        Glide.with(this)
            .load(preferences.getValue("url"))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ivSetImage)

        binding.tvUsername.text = preferences.getValue("username")


        historyDB = DatabaseHistory.getInstance(this)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.getAllHistoryObserve().observe(this,{
            adapterHistory.setHistoryData(it as ArrayList<DataHistory>)
        })

        binding.btnKalkulator.setOnClickListener {
            startActivity(Intent(this, KalkulatorActivity::class.java))
        }
    }

    private fun swipeDelete(){

        val swipeToDeleteCallback = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                GlobalScope.async {
                    val position = viewHolder.adapterPosition
                    val dataDelete = adapterHistory.deleteHistory(adapterHistory.getHistory(position), position)
                    Log.d(TAG, "onSwiped: ${dataDelete}")
                    runOnUiThread {
                        historyVm()
                    }
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvHistory)
    }

    fun historyVm(){
        adapterHistory = HistoryAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHistory.adapter = adapterHistory
    }

    override fun onResume() {
        super.onResume()
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHistory.adapter = adapterHistory
    }

}