package com.example.miraclemorning.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ScheduleDBHelper(context: Context) :
    SQLiteOpenHelper(context, "schedule.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE schedule (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date TEXT," +
                    "time TEXT," +
                    "content TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS schedule")
        onCreate(db)
    }

    // insertSchedule 함수 정의 (Long 반환)
    fun insertSchedule(date: String, time: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date", date)
            put("time", time)
            put("content", content)
        }
        return db.insert("schedule", null, values) // 성공: rowId, 실패: -1
    }

    fun getSchedulesByDate(date: String): List<Schedule> {
        val db = readableDatabase
        val cursor = db.query(
            "schedule",
            arrayOf("id", "date", "time", "content"),
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
            list.add(Schedule(id, date, time, content))
        }
        cursor.close()
        return list
    }
    // ScheduleDBHelper 내부의 권장 형태
    fun updateSchedule(id: Long, date: String, time: String, content: String) {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("date", date)
            put("time", time)
            put("content", content)
        }
        db.update("schedules", values, "id=?", arrayOf(id.toString()))
    }

    fun deleteSchedule(id: Long) {
        writableDatabase.delete("schedule", "id=?", arrayOf(id.toString()))
    }
}
