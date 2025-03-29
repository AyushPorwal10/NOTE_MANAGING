package com.example.novanotes.pdf

import android.graphics.RectF

data class SearchResults(
    val page: Int,
    val results: List<RectF>
)