package com.project.imageneer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class GuessData(
    var id: String = "",
    var imageUrl: String = "",
    var kunciJawaban: String = ""
)

@Entity(tableName = "solo")
data class SoalSoloEntity(
    @PrimaryKey
    val id: String,
    val imageName: String,
    val kunciJawaban: String
)

data class MatchHistory(
    val id: String = "",
    val date: String = "",
    val myName: String = "",
    val enemyName: String = "",
    val myScore: Int = 0,
    val enemyScore: Int = 0
)