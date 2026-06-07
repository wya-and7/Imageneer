package com.project.imageneer.Admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.project.imageneer.data.SoalSolo

class AdminViewModel : ViewModel() {

    private val storage = FirebaseStorage.getInstance()

    private val database =
        FirebaseDatabase.getInstance()
            .reference
            .child("solo")

    // Upload Image dan Kata Kunci
    fun uploadImage(
        imageUri: Uri,
        answer: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        if (answer.isBlank()) {
            onFailure(Exception("Jawaban tidak boleh kosong"))
            return
        }

        val fileName =
            answer.replace(" ", "_") + ".jpg"

        val imageRef =
            storage.reference
                .child("tebakan/$fileName")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {

                val soalId =
                    database.push().key
                        ?: return@addOnSuccessListener

                val solo = SoalSolo(
                    id = soalId,
                    imageUrl = "tebakan/$fileName",
                    kunciJawaban = answer
                )

                database.child(soalId)
                    .setValue(solo)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    // Menampilkan daftar soal
    fun getSoal(
        onResult: (List<SoalSolo>) -> Unit
    ) {
        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val listSoal =
                        mutableListOf<SoalSolo>()

                    for (data in snapshot.children) {
                        val soal =
                            data.getValue(
                                SoalSolo::class.java
                            )

                        if (soal != null) {
                            listSoal.add(soal)
                        }
                    }
                    onResult(listSoal)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )
    }

    // Hapus soal dan gambar dari Storage
    fun deleteSoal(
        soal: SoalSolo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        storage.reference
            .child(soal.imageUrl)
            .delete()
            .addOnSuccessListener {

                database.child(soal.id)
                    .removeValue()
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }

            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}