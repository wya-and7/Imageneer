package com.project.imageneer.data

data class GuessData(
    val gambar: Int,
    val jawaban: String
)

data class SoalSolo(
    var id: String = "",
    var imageUrl: String = "",
    var kunciJawaban: String = ""
)