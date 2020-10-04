package com.roshi.runningtrackerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.db.RunData
import com.roshi.runningtrackerapp.other.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {
    val diffCallBack = object : DiffUtil.ItemCallback<RunData>() {
        override fun areItemsTheSame(oldItem: RunData, newItem: RunData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RunData, newItem: RunData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)
    fun submitList(list: List<RunData>) =differ.submitList(list)

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_run,parent,false))
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val runData=differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(runData.image).into(ivRunImage)
            val calendar=Calendar.getInstance().apply {
                timeInMillis=runData.timeStamp
            }
            val dateFormat=SimpleDateFormat("dd.MM.YY", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)
            val avgSpeed="${runData.averageSpeed}km/h"
            tvAvgSpeed.text=avgSpeed
            val distanceInKm="${runData.distanceInMeters/1000f}km"
            tvDistance.text=distanceInKm
            tvTime.text=TrackingUtility.getFormattedStopWatchTime(runData.timeInmilis,false)
            val caloriesBurned="${runData.caloriesBurned}Kcal"
            tvCalories.text=caloriesBurned

        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}