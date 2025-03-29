package com.example.novanotes.pdf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OpenPdf() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        PdfViewerScreen(
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}