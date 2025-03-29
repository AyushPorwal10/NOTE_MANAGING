package com.error4.to_dolist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.novanotes.FetchFiles
import com.example.novanotes.R

@Composable
fun UserProfileData(logOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileHeader()
        Spacer(modifier = Modifier.height(20.dp))

        FileListScreen()
        LogoutButton(logOutUser = {
            logOut()
        })
        Spacer(modifier = Modifier.weight(1f))

    }
}


@Composable
fun FileListScreen() {
    val context = LocalContext.current
    var fileList by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFileContent by remember { mutableStateOf<String?>(null) }

    // Fetch files on launch
    LaunchedEffect(Unit) {
        FetchFiles.fetchAllFilesFromFirebase { files ->
            fileList = files
        }
    }
        LazyColumn() {
            items(fileList){fileName->
                FileItem(fileName) {
                    FetchFiles.fetchFileContent(fileName) { content ->
                        Log.d("Heart","COntext is ${content.toString()}")
                        selectedFileContent = content
                    }
                }
            }
        }

        selectedFileContent?.let { content ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
                    .background(Color.LightGray)
                    .padding(16.dp)
            ) {
                Text(
                    text = content,
                    fontSize = 16.sp
                )
            }
        }

}

@Composable
fun FileItem(fileName: String, onViewClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fileName,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp
            )

            Button(onClick = onViewClicked) {
                Text("View")
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile), // Replace with your image name
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = "Ayush Porwal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Total notes :256 Notes", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LogoutButton(logOutUser: () -> Unit) {
    Button(
        onClick = {
            logOutUser()
        },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
    ) {
        Text(text = "Log Out", color = Color.White)
    }
}