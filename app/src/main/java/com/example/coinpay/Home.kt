package com.example.coinpay

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coinpay.ui.theme.CoinPayTheme
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.random.Random

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinPayTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    HomeScreen()
                }
            }
        }
    }
}

enum class NavigationBarItems(val icon: ImageVector){
    Home(Icons.Filled.Home),
    Wallet(Icons.Filled.List),
    Profile(Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun HomeScreen(navController: NavController, db: FirebaseFirestore, username: String){
    val navigationBarItems = remember { NavigationBarItems.values() }
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {

                    }) {

                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "More",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 60.dp)
            ) {
                GreetingSection()
                HomeContent(db)
                Spacer(modifier = Modifier.height(16.dp))
                RecentTransactions(db = db)
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

@Composable
fun GreetingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(text = "Good Morning, User!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Here's your financial summary for today.", fontSize = 12.sp)
    }
}

@Composable
fun HomeContent(db: FirebaseFirestore) {
    val context = LocalContext.current
    val username = getUsernameFromPreferences(context)
    val balanceState = remember { mutableStateOf("") }
    val incomeState = remember { mutableStateOf("") }
    val withdrawalState = remember { mutableStateOf("") }

    // Fetch balance, income, and withdrawal amounts from Firebase
    LaunchedEffect(username) {
        username?.let {
            db.collection("users")
                .document(it)
                .collection("transactions")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        var totalBalance = 0.0
                        var totalIncome = 0.0
                        var totalWithdrawal = 0.0

                        for (document in documents) {
                            val amount = document.getDouble("amount") ?: 0.0
                            val type = document.getString("type")

                            if (type == "Deposit") {
                                totalIncome += amount
                            } else if (type == "Withdraw") {
                                totalWithdrawal += amount
                            }
                            totalBalance = totalIncome - totalWithdrawal

                            Log.d(TAG, "Amount: $amount, Type: $type")
                        }

                        balanceState.value = totalBalance.toString()
                        incomeState.value = totalIncome.toString()
                        withdrawalState.value = totalWithdrawal.toString()

                        Log.d(TAG, "Balance: ${balanceState.value}, Income: ${incomeState.value}, Withdrawal: ${withdrawalState.value}")
                    } else {
                        Log.d(TAG, "No transaction documents found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching transaction data", exception)
                }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C14DD)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Balance:",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${balanceState.value}",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(3f)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_withdraw),
                            contentDescription = "Income"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Income", color = Color.White, fontSize = 16.sp)
                            Text(text = "$${incomeState.value}", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.weight(3f)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_income),
                            contentDescription = "Withdraw"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Withdraw", color = Color.White, fontSize = 16.sp)
                            Text(text = "$${withdrawalState.value}", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}



fun getUsernameFromPreferences(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("CoinPayPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("username", null)
}




@Composable
fun RecentTransactions(db: FirebaseFirestore) {
    val context = LocalContext.current
    val username = getUsernameFromPreferences(context)

    if (username.isNullOrEmpty()) {
        Text("No username found in preferences.", color = Color.Red)
        return
    }

    var transactions by remember { mutableStateOf(emptyList<Map<String, Any>>()) }

    LaunchedEffect(username) {
        db.collection("users")
            .document(username)
            .collection("transactions")
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val fetchedTransactions = documents.map { it.data }
                transactions = fetchedTransactions
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching recent transactions", exception)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text("Your Recent Transactions:", color = Color.Black)

        if (transactions.isEmpty()) {
            Text("No recent transactions found.", color = Color.Gray)
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(
                        transactionName = transaction["title"] as? String ?: "Unknown",
                        amount = (transaction["amount"] as? Double)?.toString() ?: "0.0",
                        isDeposit = (transaction["type"] as? String) == "Deposit",
                        dateTime = (transaction["dateTime"] as? String) ?: "Unknown"
                    )
                }
            }
        }
    }
}


@Composable
fun TransactionItem(transactionName: String, amount: String, isDeposit: Boolean, dateTime: String) {
    Card(
        colors = CardDefaults.cardColors(Color(0xFF2C14DD)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on deposit or withdrawal
            val icon = if (isDeposit) {
                painterResource(id = R.drawable.ic_withdraw)
            } else {
                painterResource(id = R.drawable.ic_send)
            }
            Image(
                painter = icon,
                contentDescription = if (isDeposit) "Deposit" else "Withdraw",
                modifier = Modifier.size(24.dp),
                alignment = Alignment.CenterStart
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = transactionName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Column {
                // Displaying amount with a sign (+ or -) based on deposit or withdrawal
                val sign = if (isDeposit) "+" else "-"
                Text(
                    text = "$sign$$amount",
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    color = if (isDeposit) Color(0XFF1E7832) else Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp) // Add some padding to the right
                )

                // Displaying random date within the past 30 days
                val randomDate = generateRandomDate()
                Text(
                    text = "Date: $randomDate",
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp) // Add some padding to the right
                )
            }

        }
    }
}

// Function to generate random date within the past 30 days
private fun generateRandomDate(): String {
    val randomDays = Random.nextInt(1, 30)
    return "May ${24 - randomDays}, 2024"
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CoinPayTheme {
//        HomeScreen()
    }
}
