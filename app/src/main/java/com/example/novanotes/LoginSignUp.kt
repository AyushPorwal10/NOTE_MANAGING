package com.example.novanotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.novanotes.screens.LoginScreen
import com.example.novanotes.screens.SignUpForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class LoginSignUp : ComponentActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{

            if(firebaseAuth.currentUser != null){
                val intent = Intent(this@LoginSignUp ,  MainActivity::class.java)
                intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            val coroutineScope = rememberCoroutineScope()

            val currentScreen = remember { mutableStateOf("login") }

            if (currentScreen.value == "login") {
                LoginScreen(openSignUpScreen = {
                    currentScreen.value = "signup"
                }, signInUser = {email , password->
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Toast.makeText(this@LoginSignUp, "Login Successfull", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginSignUp ,  MainActivity::class.java)
                                intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                when (task.exception) {
                                    is FirebaseAuthInvalidUserException -> {
                                        Toast.makeText(this@LoginSignUp, "No account found with this email. Please sign up.", Toast.LENGTH_SHORT).show()
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Toast.makeText(this@LoginSignUp, "Invalid password. Try again.", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Toast.makeText(this@LoginSignUp, task.exception?.message ?: "Login failed. Try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                })
            }

            if (currentScreen.value == "signup") {
                SignUpForm(openLoginScreen = {
                    currentScreen.value = "login"
                },
                    signUpUser = {email , pass->
                        coroutineScope.launch {
                            firebaseAuth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val signInMethods = task.result?.signInMethods ?: emptyList()
                                        if (signInMethods.isNotEmpty()) {
                                            Toast.makeText(this@LoginSignUp, "Email already in use. Please try another email.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                                .addOnCompleteListener { signUpTask ->
                                                    if (signUpTask.isSuccessful) {

                                                        Toast.makeText(this@LoginSignUp, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                                                        Log.d("SignUp", "Sign-up successful!")
                                                        val intent = Intent(this@LoginSignUp ,  MainActivity::class.java)
                                                        intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Log.d("SignUp", "Sign-up failed: ${signUpTask.exception?.message}")
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.d("SignUp", "Sign-up failed: ${e.message}")
                                                }

                                        }
                                    } else {
                                        Log.d("SignUp", "Error checking email: ${task.exception?.message}")
                                    }
                                }
                        }
                    })
            }

        }
    }
}

