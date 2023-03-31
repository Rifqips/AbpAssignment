package com.rifqipadisiliwangi.assigmentandroid.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rifqipadisiliwangi.assigmentandroid.databinding.ItemHistoryBinding
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
class HistoryAdapter(var listHistory : List<DataHistory>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var dbHistory: DatabaseHistory? = null

    class ViewHolder(var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


}