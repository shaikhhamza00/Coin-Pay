package com.example.coinpay

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
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
import com.google.firebase.firestore.firestore

class Signup : ComponentActivity() {}

@Composable
fun SignupScreen(navController: NavController) {
    // MutableState variables for user input
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Firestore instance
    val db = Firebase.firestore

    // Local context for accessing SharedPreferences
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            text = "Create an account"
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            color = Color(0XFF6F6F6F),
            text = "Enter your Mobile Number to verify your account",
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                focusedLabelColor = Color(0XFF2C14DD),
                errorContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "Phone") },
            onValueChange = {
                phone = it
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { /* Handle next action */ }
            ),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.phone),
                    contentDescription = "Phone Icon",
                    tint = Color.Unspecified
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            maxLines = 1,
            value = email,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Color(0XFF2C14DD),
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "Email") },
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { /* Handle next action */ }
            ),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.email),
                    contentDescription = "Email Icon",
                    tint = Color.Unspecified
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Color(0XFF2C14DD),
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "PIN") },
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.pin),
                    contentDescription = "PIN Icon",
                    tint = Color.Unspecified
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedLabelColor = Color(0XFF2C14DD),
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0XFF2C14DD)
            ),
            label = { Text(text = "Confirm PIN") },
            onValueChange = {
                confirmPassword = it
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.pin),
                    contentDescription = "Confirm PIN Icon",
                    tint = Color.Unspecified
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))

        // Display Snackbar for PIN validation error
        if (password.length != 4 || confirmPassword != password) {
            Snackbar(
                action = {
                    Button(
                        onClick = {
                            // Dismiss Snackbar
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(if (password.length != 4) "PIN must be 4 digits" else "PIN confirmation failed")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFF2C14DD),
                contentColor = Color.White
            ),
            onClick = {
                // Check if all fields are filled and passwords match
                if (phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                    // Create a user object
                    val user = hashMapOf(
                        "phone" to phone,
                        "email" to email,
                        "password" to password
                    )

                    // Add user to Firestore database
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            // Save username to preferences
                            saveUsernameToPreferences(context, phone)

                            // Navigate to Setup screen on successful signup
                            navController.navigate("Setup")
                        }
                        .addOnFailureListener { e ->
                            // Show error message if signup fails
                            println("Error adding document: $e")
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Signup")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(color = Color(0XFF6F6F6F)))
                append("Already have an account?   ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0XFF2C14DD)))
                append("Login")
                pop()
                toAnnotatedString()
            },
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("Login")
                }
                .padding(horizontal = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

private fun saveUsernameToPreferences(context: Context, username: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("CoinPayPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("username", username)
    editor.apply()
}


@Composable
fun setupDone(navController: NavController){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(2f))
        Image(painter = painterResource(id = R.drawable.setup_done),
            contentDescription = "Account Setup Done")
        Spacer(modifier = Modifier.weight(1f))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFF2C14DD),
                contentColor = Color.White
            ),
            onClick = {
                navController.navigate("Home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Get Started")
        }
    }
}

