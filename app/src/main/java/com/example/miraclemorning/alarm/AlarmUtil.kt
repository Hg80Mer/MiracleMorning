package com.example.miraclemorning.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmUtil {

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(
        context: Context,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        content: String,
        requestCode: Int // ← DB insert id 전달
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("content", content)
        }

        // requestCode를 고유값으로 사용
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0)
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
