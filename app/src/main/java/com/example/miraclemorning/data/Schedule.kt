package com.example.miraclemorning.data

data class Schedule(
    val id: Long,
    val date: String,
    val time: String,
    val content: String,
    val isDone: Int
)