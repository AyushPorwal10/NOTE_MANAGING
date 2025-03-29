package com.example.novanotes.screens

import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.novanotes.ChatViewModel
import com.example.novanotes.FirebasePDFUploader
import com.example.novanotes.R
import com.example.novanotes.geminichatbot.ChatPage
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryAndAddNote(chatViewModel: ChatViewModel) {

    Scaffold(
        topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.White
            ),
            title = {
                Text(
                    "NovaNotes", modifier = Modifier.padding(start = 16.dp), color = Color.Black
                )
            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            },
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Gray)) // Small border
                .padding(2.dp)

        )
    }
    ) { paddingValues ->

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") { BtnForSummaryAndUploadNote(navController) }
                composable("addNotePdf") { AddNotePdf() }
                composable("summary") { Summary(chatViewModel) }
            }


        }
    }
}

@Composable
fun BtnForSummaryAndUploadNote(navController: NavHostController) {

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (card1, card2) = createRefs()

        Card(
            modifier = Modifier
                .constrainAs(card1) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .padding(8.dp)
                .clickable { navController.navigate("addNotePdf") },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Text(
                text = "Upload Note",
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        }

        Card(
            modifier = Modifier
                .constrainAs(card2) {
                    start.linkTo(card1.end, margin = 8.dp)
                    top.linkTo(parent.top)
                }
                .padding(8.dp)
                .clickable { navController.navigate("summary") },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Text(
                text = "AI Notes",
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        }

        // Center the cards horizontally
        createHorizontalChain(card1, card2, chainStyle = ChainStyle.Packed)
    }

}

@Composable
fun AddNotePdf() {

    val context = LocalContext.current
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var showProgressBar by remember { mutableStateOf(false) }
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPdfUri = uri
    }
    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            pdfPickerLauncher.launch("application/pdf")
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black )) {
            Text(text = "Choose note pdf", color = Color.White)
        }

        var noteName by remember { mutableStateOf("") }
        var noteTopic by remember { mutableStateOf("") }

            OutlinedTextField(
                value = noteName,
                onValueChange = { noteName = it },
                label = { Text("Note Name") },
                placeholder = { Text("Enter note name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = noteTopic,
                onValueChange = { noteTopic = it },
                label = { Text("Note Topic") },
                placeholder = { Text("Enter note topic") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

        selectedPdfUri?.let { uri ->

            PDFRow(uri, onRemove = { selectedPdfUri = null })

            Button(
                onClick = {

                    if(noteName.trim().isNotEmpty() && noteTopic.trim().isNotEmpty()){
                        FirebasePDFUploader.uploadPdf(
                            noteName,
                            noteTopic,
                            uri, context,
                            onUploadProgress = { progress ->
                                showProgressBar = true
                            },
                            onUploadComplete = { downloadUrl ->
                                noteName = ""
                                noteTopic = ""
                                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                showProgressBar = false
                            }
                        )
                    }
                    else {
                        Toast.makeText(context, "Note name and Note topic is required", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {

                if(showProgressBar){
                    CircularProgressIndicator(modifier = Modifier.size(26.dp) , color = Color.White)
                }
                else{
                    Text("Add Notes")
                }

            }
        }
    }
}

@Composable
fun Summary(chatViewModel: ChatViewModel) {
    ChatPage(viewModel = chatViewModel)
}
