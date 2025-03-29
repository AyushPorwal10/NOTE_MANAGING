package com.example.novanotes

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel(){


    private val _firebaseFirestoreInstance = FirebaseFirestore.getInstance()

    private val homescreenRepo = HomeScreenRepo()

    val firebaseFirestoreInstance: FirebaseFirestore
        get() = _firebaseFirestoreInstance

    private val _notesList = MutableStateFlow<List<Notes>>(emptyList())

    val notesList : StateFlow<List<Notes>>
        get() = _notesList.asStateFlow()

    fun fetchNotesList(){
        viewModelScope.launch {
            homescreenRepo.fetchRecentNotes(firebaseFirestoreInstance)
                .catch {
                    Log.d("FetchingNotes","Error Fetching Roles")
                }
                .collect{
                    _notesList.value = it
                }
        }

    }
}