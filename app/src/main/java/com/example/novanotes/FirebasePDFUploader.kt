package com.example.novanotes

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.novanotes.helper.CheckEmptyFields
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object FirebasePDFUploader {
    fun uploadPdf(
        noteName : String ,
        noteTopic : String ,
        pdfUri: Uri,
        context: Context,

        onUploadProgress: (Float) -> Unit,
        onUploadComplete: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("pdfs/${UUID.randomUUID()}.pdf")

        val uploadTask = storageRef.putFile(pdfUri)

        uploadTask.addOnProgressListener { snapshot ->
            val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
            onUploadProgress(progress)
        }

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUrl ->

                savePdfUrlToFirestore(downloadUrl.toString(), context , noteTopic , noteName)
                Log.d("NoteData","Download url is received")
                onUploadComplete(downloadUrl.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePdfUrlToFirestore(downloadUrl: String, context: Context , noteTopic : String , noteName : String ) {



        val firestore = FirebaseFirestore.getInstance()
        val pdfData =  downloadUrl
        Log.d("NoteData","Topic -> $noteTopic , Name -> $noteName , url is ${pdfData.toString()}")
        val notesDataToAdded =  Notes(noteTopic,noteName,CheckEmptyFields.getCurrentTime(),"Maths",pdfData)


        firestore.collection("pdfs")
            .add(notesDataToAdded)
            .addOnSuccessListener {
                Toast.makeText(context, "Notes addedd successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }

    }
}
