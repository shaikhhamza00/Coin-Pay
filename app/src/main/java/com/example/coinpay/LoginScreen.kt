package com.example.coinpay

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint.Style
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coinpay.ui.theme.CoinPayTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(navController = null) // Replace null with your NavController instance
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController?) {
    var username by remember { mutableStateOf("") }
    val PHONE_NUMBER_REGEX = "[0-9]{11}" // Assuming PHONE_NUMBER_REGEX is a valid phone number pattern
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var loginButtonClicked by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController?.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            style = TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = "Login to your account"
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            color = Color(0XFF6F6F6F),
            text = "Enter your mobile number or email and PIN",
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0XFF2C14DD),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "Mobile Number or Email") },
            isError = usernameError,
            onValueChange = {
                username = it
                // Validation logic for username (mobile or email)
                usernameError = it.isEmpty()
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { }
            ),
            leadingIcon = {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.phone),
                    contentDescription = "Phone Icon",
                    tint = Color.Unspecified)
            }

        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0XFF2C14DD),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "PIN") },
            isError = passwordError,
            onValueChange = {
                password = it
                // Validation logic for password (PIN)
                passwordError = it.length != 4 || !it.all { char -> char.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.pin),
                    contentDescription = "PIN Icon",
                    tint = Color.Unspecified
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    val eyeIcon = if (passwordVisibility) {
                        R.drawable.visibility
                    } else {
                        R.drawable.visibility_off
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(id = eyeIcon),
                        contentDescription = "Toggle password visibility",
                        tint = Color.Unspecified
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display Snackbar for login validation error
        if ((usernameError || passwordError) && loginButtonClicked) {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {
                    Button(
                        onClick = {
                            // Dismiss Snackbar
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text("Invalid username or password")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Login button and "Don't have an account? Signup" text
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFF2C14DD),
                    contentColor = Color.White
                ),
                onClick = {
                    loginButtonClicked = true
                    if (username.isNotEmpty() && password.isNotEmpty() && password.length == 4 && password.all { it.isDigit() }) {
                        // Check if username is a phone number or email
                        if (username.matches(Regex(PHONE_NUMBER_REGEX))) { // Assuming PHONE_NUMBER_REGEX is a valid phone number pattern
                            Log.d("LoginScreen", "Trying login with phone number: $username")
                            // Login with phone number
                            val userDocRef = db.collection("users").whereEqualTo("phone", username.trim()).limit(1)

                            userDocRef.get().addOnCompleteListener { task ->
                                if (task.isSuccessful && task.result?.isEmpty() == false) {
                                    val userDocument = task.result?.documents?.first()
                                    if (userDocument?.getString("password") == password) {
                                        // Login successful, store username in SharedPreferences and navigate to Home screen
                                        Log.d("LoginScreen", "Login successful with phone number")
                                        saveUsernameToPreferences(context, username)
                                        navController?.navigate("Home")
                                    } else {
                                        // Invalid password
                                        passwordError = true
                                        Log.d("LoginScreen", "Invalid password for phone number login")
                                    }
                                } else {
                                    // User not found with the phone number
                                    usernameError = true
                                    Log.d("LoginScreen", "User not found with phone number: $username")
                                }
                            }
                        } else {
                            Log.d("LoginScreen", "Trying login with email: $username")
                            // Login with email
                            auth.signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Login successful with email, store username in SharedPreferences and navigate to Home screen
                                        Log.d("LoginScreen", "Login successful with email")
                                        saveUsernameToPreferences(context, username)
                                        navController?.navigate("Home")
                                    } else {
                                        val error = task.exception?.localizedMessage ?: "Login failed"
                                        Log.e("LoginScreen", "Login error with email: $error")
                                        // ... handle login error (e.g., show Snackbar)
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(color = Color(0XFF6F6F6F)))
                    append("Don't have an account?   ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0XFF2C14DD)))
                    append("Signup")
                    pop()
                    toAnnotatedString()
                },
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController?.navigate("Signup")
                    }
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun saveUsernameToPreferences(context: Context, username: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("CoinPayPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("username", username)
    editor.apply()
}
