package com.android.example.modul8

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class StudentViewModel : ViewModel() {
    private val db = Firebase.firestore
    var students by mutableStateOf(emptyList<Student>())

    init { fetchStudents() }

    // Add student + phones to subcollection
    fun addStudent(student: Student, phones: List<String>) {
        db.collection("students")
            .add(student)
            .addOnSuccessListener { studentRef ->
                if (phones.isNotEmpty()) {
                    addPhonesToStudent(studentRef.id, phones)
                }
                fetchStudents() // Refresh list
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding student", e)
            }
    }

    // Batch-write phones to subcollection
    private fun addPhonesToStudent(studentId: String, phones: List<String>) {
        val batch = db.batch()
        phones.forEach { number ->
            val phoneRef = db.collection("students")
                .document(studentId)
                .collection("phones")
                .document() // Auto-ID
            batch.set(phoneRef, mapOf("number" to number))
        }
        batch.commit().addOnFailureListener { e ->
            Log.e("Firestore", "Error adding phones", e)
        }
    }

    // Fetch all students (without phones)
    private fun fetchStudents() {
        db.collection("students").get()
            .addOnSuccessListener { result ->
                students = result.toObjects(Student::class.java)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching students", e)
            }
    }

    // Fetch phones for one student
    suspend fun getStudentPhones(studentId: String): List<String> {
        return try {
            db.collection("students")
                .document(studentId)
                .collection("phones")
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("number") }
        } catch (e: Exception) {
            emptyList()
        }
    }
}