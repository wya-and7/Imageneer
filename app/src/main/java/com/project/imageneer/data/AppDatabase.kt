package com.project.imageneer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SoalSoloEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun soalSoloLocalDao(): SoalSoloLocalDao

    companion object {
        @Volatile // Gunakan @Volatile agar sinkronisasi thread lebih aman dibandingkan @Transient
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "imageneer_database"
                )
                    // Menggunakan provider untuk mengoper instance database yang baru dibuat secara aman
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Eksekusi pengisian data menggunakan coroutine global scope offline
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).soalSoloLocalDao().insertAll(
                                    listOf(
                                        SoalSoloEntity("1", "abuabu", "Abu Abu"),
                                        SoalSoloEntity("2", "buahlil", "Buah Lil"),
                                        SoalSoloEntity("3", "jusfriend", "jus Friend"),
                                        SoalSoloEntity("4", "kotakmakan", "Kotak Makan"),
                                        SoalSoloEntity("5", "nononobaby", "No no no baby"),
                                        SoalSoloEntity("6", "orangdesa", "Orang Desa"),
                                        SoalSoloEntity("7", "sapiterbang", "Sapi Terbang"),
                                        SoalSoloEntity("8", "tunggukiris", "Tunggu Kiris"),
                                        SoalSoloEntity("9", "ubiungi", "Ubi Ungi"),
                                        SoalSoloEntity("10", "ujangkedu", "Ujang Kedu")
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}