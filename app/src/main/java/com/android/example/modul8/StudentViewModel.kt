package com.android.example.modul8

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class StudentViewModel : ViewModel() {
    private val db = Firebase.firestore
    var students by mutableStateOf(listOf<Student>())
        private set
    init {
        fetchStudents()
    }
    fun addStudent(student: Student) {
        val studentMap = hashMapOf(
            "id" to student.id,
            "name" to student.name,
            "program" to student.program
            "phones" to student.phones
        )
        db.collection("students")
            .add(studentMap)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot added with ID:
                    ${it.id}")
                fetchStudents() // Refresh list
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }
    private fun fetchStudents() {
        db.collection("students")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Student>()
                for (document in result) {
                    val id = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val program = document.getString("program") ?: ""
                    val phones = document.get("Phones") as? List<String> ?: emptyList()
                    list.add(Student(id, name, program))
                }
                students = list
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.",
                    exception)
            }
    }
}

@Composable
fun TextField(value: Any, onValueChange: () -> Unit, label: () -> Unit) {

}

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel =
                                  viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        TextField(value = studentId, onValueChange = { studentId = it },
            label = { Text("Student ID") })
        TextField(value = name, onValueChange = { name = it }, label = {
            Text("Name") })
        TextField(value = program, onValueChange = { program = it },
            label = { Text("Program") })
        Button(
            onClick = {
                viewModel.addStudent(Student(studentId, name, program))
                studentId = ""
                name = ""
                program = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Submit")
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text("Student List", style =
        MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(viewModel.students) { student ->
                Text("${student.id} - ${student.name} -
                    ${student.program}")
            }
        }
    }
}

