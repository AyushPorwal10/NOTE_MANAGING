package com.example.novanotes.geminichatbot

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.novanotes.ChatViewModel
import com.example.novanotes.R
import com.example.novanotes.ui.theme.CurrentUserMessageColor
import com.example.novanotes.ui.theme.ModelMessageColor
import com.example.novanotes.ui.theme.Purple80
import com.example.novanotes.ui.theme.lighWhite
import com.example.novanotes.ui.theme.lighWhite
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

@RequiresApi(35)
@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = modifier
    ) {
        //AppHeader()
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            }
        )
    }
}


@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.chat_icon),
                contentDescription = "Icon",
                tint = Purple80,
            )
            Text(text = "Ask me anything related to your notes", fontSize = 22.sp, maxLines = 2)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                MessageRow(messageModel = it)
            }
        }
    }


}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    val context = LocalContext.current
    var isNoteSaving by remember {
        mutableStateOf(false)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) ModelMessageColor else CurrentUserMessageColor)
                    .padding(16.dp)
            ) {

                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }


            }

            if(isModel){
                if(isNoteSaving){
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
                else{

                    IconButton(
                        onClick = {
                            isNoteSaving = true
                            saveAndUploadTextToFirebase(messageModel.message, context , onNoteSaved = {
                                isNoteSaving = false
                            })
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.save_note),
                            contentDescription = "Save Response",
                            tint = Color.Black
                        )
                    }
                }
            }


        }
    }
}


fun saveAndUploadTextToFirebase(text: String, context: Context , onNoteSaved : () -> Unit) {

    val fileName = "AI Response${System.currentTimeMillis()}.txt"
    val file = File(context.getExternalFilesDir(null), fileName)

    try {
        file.writeText(text)

        val storageRef = FirebaseStorage.getInstance().reference
        val fileUri = Uri.fromFile(file)
        val fileRef = storageRef.child("ai_responses/${file.name}") // Save in "ai_responses" folder

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Toast.makeText(context, "Note Saved Successfully", Toast.LENGTH_LONG).show()
                    onNoteSaved()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
            }

    } catch (e: IOException) {
        Toast.makeText(context, "Failed to save response", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

fun getFirstTwoWords(text: String): String {
    val words = text.split("\\s+".toRegex()) // Split by whitespace (handles multiple spaces)
    return words.take(2).joinToString(" ") // Take first 2 words and join them back
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {

    var message by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            placeholder = {
                Text(text = "Your note assistant", fontWeight = FontWeight.Light)
            },
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }

        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}
