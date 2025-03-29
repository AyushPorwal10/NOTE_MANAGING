package com.example.novanotes

import android.util.Log
import com.google.firebase.storage.FirebaseStorage

object FetchFiles {

    fun fetchAllFilesFromFirebase(onFilesFetched: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("ai_responses")

        storageRef.listAll()
            .addOnSuccessListener { result ->
                val fileNames = result.items.map { it.name }
                onFilesFetched(fileNames)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to fetch files")
            }
    }

    fun fetchFileContent(fileName: String, onContentFetched: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("ai_responses/$fileName")

        storageRef.getBytes(1024 * 1024) // Max 1MB
            .addOnSuccessListener { bytes ->
                val content = String(bytes, Charsets.UTF_8)
                onContentFetched(content)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to fetch file content")
            }
    }

}