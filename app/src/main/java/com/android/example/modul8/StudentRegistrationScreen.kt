package com.android.example.modul8

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var currentPhone by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(emptyList<String>()) }
    var expandedStudentId by remember { mutableStateOf<String?>(null) }
    var studentPhones by remember { mutableStateOf(emptyList<String>()) }

    // Load phones when a student is expanded
    LaunchedEffect(expandedStudentId) {
        expandedStudentId?.let { id ->
            studentPhones = viewModel.getStudentPhones(id)
        }
    }

    Column(Modifier.padding(16.dp)) {
        // Student form (unchanged from your original)
        // ...

        // Phone number input
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = currentPhone,
                onValueChange = { currentPhone = it },
                label = { Text("Phone") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (currentPhone.isNotBlank()) {
                        phoneList = phoneList + currentPhone
                        currentPhone = ""
                    }
                }
            ) { Text("+") }
        }

        // Display added phones
        phoneList.forEach { phone ->
            Text("• $phone")
        }

        // Submit button
        Button(
            onClick = {
                viewModel.addStudent(
                    Student(studentId, name, program),
                    phoneList
                )
                // Reset form
                studentId = ""
                name = ""
                program = ""
                phoneList = emptyList()
            }
        ) { Text("Submit") }

        // Student list with expandable phones
        LazyColumn {
            items(viewModel.students) { student ->
                Card(
                    modifier = Modifier.run {
                        padding(8.dp)
                                        .fillMaxWidth()
                    }
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${student.id}: ${student.name}")
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    expandedStudentId = if (expandedStudentId == student.id) null else student.id
                                }
                            ) {
                                Icon(
                                    imageVector = if (expandedStudentId == student.id)
                                        Icons.Default.ExpandLess
                                    else
                                        Icons.Default.ExpandMore,
                                    contentDescription = "Toggle phones"
                                )
                            }
                        }

                        if (expandedStudentId == student.id) {
                            if (studentPhones.isEmpty()) {
                                Text("No phones", style = MaterialTheme.typography.bodySmall)
                            } else {
                                Column {
                                    studentPhones.forEach { phone ->
                                        Text("☎ $phone")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}