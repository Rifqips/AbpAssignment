package com.rifqipadisiliwangi.assigmentandroid.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.rifqipadisiliwangi.assigmentandroid.databinding.ItemHistoryBinding
import com.rifqipadisiliwangi.assigmentandroid.room.DaoHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryAdapter(): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var context : Context
    var dbHistory: DatabaseHistory? = null
    private var listHistory : List<DataHistory> = emptyList()
    private lateinit var daoHistory : DaoHistory

    class ViewHolder(var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        dbHistory = DatabaseHistory.getInstance(parent.context)
        if(dbHistory != null){
            daoHistory = dbHistory!!.HistoryItem()
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvValueSatu.text = listHistory[position].valueSatu
        holder.binding.tvValueDua.text = listHistory[position].valueDua
        holder.binding.tvMetode.text = listHistory[position].metode
        holder.binding.tvHasil.text = listHistory[position].hasil
    }

    override fun getItemCount(): Int {
        return listHistory.size

    }

    fun setHistoryData(listHistory : ArrayList<DataHistory>){
        this.listHistory = listHistory
    }

    interface OnAdapterListener {
        fun onDelete(wishlist: DataHistory)
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    fun getHistory(position: Int): DataHistory = listHistory[position]

    fun deleteHistory(dataListHistory: DataHistory, position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            daoHistory.deleteHistory(dataListHistory)
        }
        notifyItemChanged(position)
    }

}