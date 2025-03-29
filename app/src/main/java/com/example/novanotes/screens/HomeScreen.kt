package com.error4.to_dolist

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import com.example.novanotes.AddNote
import com.example.novanotes.HomeScreenViewModel
import com.example.novanotes.Notes
import com.example.novanotes.R
import com.example.novanotes.UserProfile
import com.example.novanotes.helper.CheckEmptyFields


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val notesList = homeScreenViewModel.notesList.collectAsState()

    val searchNotes = remember {
        mutableStateOf("")
    }
    val itemlist = remember {
        mutableListOf(
            listOf(
                "Quantum Computing",
                "Data Structures",
                "Project Review",
                "Book Notes"
            )
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NovaNotes", modifier = Modifier.padding(start = 16.dp)
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context , AddNote::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Section

            WelComBackAndProfile(openUserProfile = {
                val intent = Intent(context , UserProfile::class.java)
                context.startActivity(intent)
            })

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Notes and Favorites
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Recent Notes", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(notesList.value){recentNote->
                    RecentNoteCard(recentNote)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smart Collections
            Text("Smart Collections", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CollectionCard("Lectures", "12 notes")
                CollectionCard("Research", "8 notes")
                CollectionCard("Projects", "5 notes")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Summary
            Text("Today's Summary", style = MaterialTheme.typography.labelSmall,fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryCard("Notes Created", "5")
                SummaryCard("Words Written", "1,245")
                SummaryCard("Time Spent", "3h 45m")
            }
        }

    }
}

@Composable
fun WelComBackAndProfile(openUserProfile : () -> Unit ) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val (welcomeText, profileImage) = createRefs()

        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.constrainAs(welcomeText) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )


        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(profileImage) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .clickable {
                    openUserProfile()
                }
                .size(40.dp)
                .clip(CircleShape)
        )

    }
}

@Composable
fun CollectionCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium , fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String) {
    Card(
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp) , verticalArrangement = Arrangement.Center) {
            Text(title, style = MaterialTheme.typography.titleSmall,fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RecentNoteCard(recentNote: Notes) {
    val context = LocalContext.current
    Box(modifier = Modifier
        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
        .padding(4.dp)
        .fillMaxWidth(0.8f), contentAlignment = Alignment.Center) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {

            val (notesTopic, notesName, lastUpdated, noteTag1, noteTag2) = createRefs()

            Text(
                text = recentNote.notesTopic + "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(notesTopic) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(4.dp))

            Text(
                text = recentNote.notesName,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(notesName) {
                        top.linkTo(notesTopic.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(4.dp))

            NotesTag(label = recentNote.noteTag, modifier = Modifier
                .heightIn(32.dp)
                .constrainAs(noteTag1) {
                    start.linkTo(parent.start)
                    top.linkTo(notesName.bottom, margin = 4.dp)
                }
                .padding(4.dp),
                openingPdf = {
                    Log.d("ShowingNote","URI is ${recentNote.pdfUrl}")
                    showPdfToUser(recentNote.pdfUrl , context)
                })


            Text(
                text = CheckEmptyFields.getTimeAgo(recentNote.lastUpdated),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(lastUpdated) {
                        top.linkTo(notesName.bottom)
                        bottom.linkTo(noteTag1.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(4.dp))

        }

    }
}

@Composable
fun NotesTag(label: String, modifier: Modifier , openingPdf : () -> Unit) {
    AssistChip(
        modifier = modifier,
        onClick = {
                  openingPdf()
        },
        label = { Text(label, style = MaterialTheme.typography.bodySmall) }
    )
}

fun showPdfToUser(pdfUrl: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No browser found to open PDF.", Toast.LENGTH_SHORT).show()
    }
}