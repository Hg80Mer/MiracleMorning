package com.example.miraclemorning.ui

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.miraclemorning.R
import com.example.miraclemorning.data.Schedule
import com.example.miraclemorning.data.ScheduleDBHelper

class ScheduleAdapter(
    private val list: MutableList<Schedule>,
    private val db: ScheduleDBHelper,
    private val refresh: () -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
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

        // [삭제 기능]
        holder.btnDelete.setOnClickListener {
            db.deleteSchedule(item.id)
            refresh()
        }

        // [수정 기능]
        holder.btnEdit.setOnClickListener {
            val timeParts = item.time.split(":")
            val initialHour = if (timeParts.size == 2) timeParts[0].toInt() else 7
            val initialMinute = if (timeParts.size == 2) timeParts[1].toInt() else 0

            TimePickerDialog(
                holder.itemView.context,
                { _, h, m ->
                    val newTime = String.format("%02d:%02d", h, m)

                    db.updateSchedule(item.id, item.date, newTime, item.content)

                    refresh()
                },
                initialHour,
                initialMinute,
                true
            ).show()
        }
    }
}