package com.example.novanotes

import android.graphics.Path.Direction
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class HomeScreenRepo {


    fun fetchRecentNotes(firebaseFirebase : FirebaseFirestore) : Flow<List<Notes>>  = callbackFlow {

        val collectionReference = firebaseFirebase.collection("pdfs").orderBy("lastUpdated" , Query.Direction.DESCENDING)


        val realTimeNotesUpdate = collectionReference.addSnapshotListener{snapshot,error->
            if(error != null){
                Log.d("FetchingNotes" , "Error in fetching notes $error")
                close(error)
                return@addSnapshotListener
            }


            if(snapshot != null ){
                val listOfNotes = snapshot.documents.mapNotNull{note->
                    note.toObject(Notes::class.java)
                } ?: emptyList()
                trySend(listOfNotes)
            }
        }

        awaitClose{realTimeNotesUpdate.remove()}
    }
}