package com.example.coinpay

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coinpay.ui.theme.CoinPayTheme
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Transaction : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinPayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController, db: FirebaseFirestore) {
    val context = LocalContext.current
    val username = getUsernameFromPreferences(context)
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isDeposit by remember { mutableStateOf(true) }
    val navigationBarItems = remember { NavigationBarItems.values() }
    var selectedIndex by remember { mutableStateOf(2) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Make A Transaction", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back arrow click */ }) {
                        Icon(
                            imageVector = TransactionDetailItems.ArrowBack.icon,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C14DD)
                )
            )
        },
        content = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C14DD),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(vertical = 60.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Enter Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Enter Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                colors = CheckboxDefaults.colors(checkedColor = Color.White),
                                checked = isDeposit,
                                onCheckedChange = { isDeposit = true }
                            )
                            Text("Deposit", modifier = Modifier.padding(start = 8.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                colors = CheckboxDefaults.colors(checkedColor = Color.White),
                                checked = !isDeposit,
                                onCheckedChange = { isDeposit = false }
                            )
                            Text("Withdraw", modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF2C14DD)),
                        onClick = {
                            // Save transaction data to Firestore
                            if (title.isNotEmpty() && amount.isNotEmpty() && !username.isNullOrEmpty()) {
                                val transactionData = hashMapOf(
                                    "title" to title,
                                    "amount" to amount.toDouble(),
                                    "type" to if (isDeposit) "Deposit" else "Withdraw",
                                    "username" to username
                                )
                                // Add a new document with a generated ID
                                db.collection("users") // Assume "users" is the collection of users
                                    .document(username) // Use the username as the document ID
                                    .collection("transactions") // Subcollection for user transactions
                                    .add(transactionData)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                        // Navigate to transaction details screen after data is saved
                                        navController.navigate("Home")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }
                            } else {
                                Log.e(TAG, "Username, title, or amount is empty")
                                // Handle the case when username, title, or amount is empty
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Complete")
                    }
                }
            }
        },
        bottomBar = {
            AnimatedNavigationBar(
                modifier = Modifier.height(64.dp),
                selectedIndex = selectedIndex,
                cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                ballAnimation = Parabolic(tween(300)),
                indentAnimation = Height(tween(300)),
                barColor = Color(0xFF2C14DD),
                ballColor = Color(0xFF2C14DD)
            ) {
                navigationBarItems.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .noRippleClickable {
                                selectedIndex = index
                                when (index) {
                                    0 -> navController.navigate("Home")
                                    1 -> navController.navigate("Transactions")
                                    2 -> navController.navigate("Transaction") // Create a profile screen if necessary
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = if (selectedIndex == index) Color.White else Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }



        )


}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    CoinPayTheme {
        TransactionScreen(navController = rememberNavController(), db = FirebaseFirestore.getInstance())
    }
}