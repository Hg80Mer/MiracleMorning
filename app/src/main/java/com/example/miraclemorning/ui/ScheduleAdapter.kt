package com.example.miraclemorning.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miraclemorning.R
import com.example.miraclemorning.data.Schedule
import com.example.miraclemorning.data.ScheduleDBHelper

class ScheduleAdapter(
    private val list: MutableList<Schedule>,
    private val db: ScheduleDBHelper,
    private val refresh: () -> Unit,
    private val onItemClick: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbDone: CheckBox = view.findViewById(R.id.cbDone)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvContent.text = item.content
        holder.tvTime.text = item.time


        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = (item.isDone == 1)

        val done = item.isDone == 1
        holder.tvContent.paintFlags =
            if (done) holder.tvContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else holder.tvContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        holder.itemView.alpha = if (done) 0.45f else 1f

        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            db.updateDone(item.id, if (isChecked) 1 else 0)
            refresh()
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }
}
