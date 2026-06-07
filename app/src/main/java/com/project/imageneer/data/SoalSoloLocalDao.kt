package com.project.imageneer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SoalSoloLocalDao {

    @Query("SELECT * FROM solo")
    suspend fun getAllSoal(): List<SoalSoloEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(soalList: List<SoalSoloEntity>)

}