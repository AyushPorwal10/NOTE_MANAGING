package com.example.novanotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.error4.to_dolist.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]

        super.onCreate(savedInstanceState)
        setContent {
            homeScreenViewModel.fetchNotesList()
            HomeScreen(homeScreenViewModel)
        }
    }
}