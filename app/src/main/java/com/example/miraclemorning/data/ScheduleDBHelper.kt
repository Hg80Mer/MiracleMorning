package com.example.miraclemorning.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleDBHelper(context: Context) :
    SQLiteOpenHelper(context, "schedule.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE schedule (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date TEXT," +
                    "time TEXT," +
                    "content TEXT," +
                    "isDone INTEGER DEFAULT 0" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE schedule ADD COLUMN isDone INTEGER DEFAULT 0")
        }
    }

    fun insertSchedule(date: String, time: String, content: String): Long {
        val values = ContentValues().apply {
            put("date", date)
            put("time", time)
            put("content", content)
            put("isDone", 0)
        }
        return writableDatabase.insert("schedule", null, values)
    }

    fun getSchedulesByDate(date: String): List<Schedule> {
        val cursor = readableDatabase.query(
            "schedule",
            arrayOf("id", "date", "time", "content", "isDone"),
            "date=?",
            arrayOf(date),
            null,
            null,
            "time ASC"
        )

        val list = mutableListOf<Schedule>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            val isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone"))
            list.add(Schedule(id, date, time, content, isDone))
        }
        cursor.close()
        return list
    }

    fun updateSchedule(id: Long, date: String, time: String, content: String) {
        val values = ContentValues().apply {
            put("date", date)
            put("time", time)
            put("content", content)
        }
        writableDatabase.update("schedule", values, "id=?", arrayOf(id.toString()))
    }

    fun updateDone(id: Long, isDone: Int) {
        val values = ContentValues().apply { put("isDone", isDone) }
        writableDatabase.update("schedule", values, "id=?", arrayOf(id.toString()))
    }

    fun deleteSchedule(id: Long) {
        writableDatabase.delete("schedule", "id=?", arrayOf(id.toString()))
    }

    /**
     * 기간 루틴 등록
    
     */
    fun insertRoutineRange(startDate: String, endDate: String, time: String, content: String): Int {
        val db = writableDatabase
        db.beginTransaction()

        var count = 0
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = Calendar.getInstance().apply { this.time = fmt.parse(startDate)!! }
            val end = Calendar.getInstance().apply { this.time = fmt.parse(endDate)!! }

            // start > end면 swap
            if (start.after(end)) {
                val tmp = start.time
                start.time = end.time
                end.time = tmp
            }

            while (!start.after(end)) {
                val d = fmt.format(start.time)

                val values = ContentValues().apply {
                    put("date", d)
                    put("time", time)
                    put("content", content)
                    put("isDone", 0)
                }

                val rowId = db.insert("schedule", null, values)
                if (rowId != -1L) count++

                start.add(Calendar.DAY_OF_MONTH, 1)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        return count
    }
}
