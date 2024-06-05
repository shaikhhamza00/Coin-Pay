package com.example.coinpay

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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


class TransactionsDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinPayTheme {
                }
            }
        }
    }


enum class TransactionDetailItems(val icon: ImageVector) {
    ArrowBack(Icons.Filled.ArrowBack)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(navController: NavController, context: Context, db: FirebaseFirestore) {
    val navigationBarItems = remember { NavigationBarItems.values() }
    var selectedIndex by remember { mutableStateOf(1) }

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("CoinPayPreferences", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    // State to hold the transaction details
    val balanceState = remember { mutableStateOf("0.0") }
    val incomeState = remember { mutableStateOf("0.0") }
    val withdrawalState = remember { mutableStateOf("0.0") }

    // Fetch transaction details from Firestore
    LaunchedEffect(username) {
        username?.let {
            db.collection("users")
                .document(it)
                .collection("transactions")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        var totalBalance = 0.0f
                        var totalIncome = 0.0f
                        var totalWithdrawal = 0.0f

                        for (document in documents) {
                            val amount = document.getDouble("amount")?.toFloat() ?: 0.0f
                            val type = document.getString("type")

                            if (type == "Deposit") {
                                totalIncome += amount
                            } else if (type == "Withdraw") {
                                totalWithdrawal += amount
                            }
                            totalBalance = totalIncome - totalWithdrawal
                        }

                        balanceState.value = totalBalance.toString()
                        incomeState.value = totalIncome.toString()
                        withdrawalState.value = totalWithdrawal.toString()

                        Log.d(TAG, "Total Balance: $totalBalance, Total Income: $totalIncome, Total Withdrawal: $totalWithdrawal")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching transaction data", exception)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Transaction Detail", color = Color.White, fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularChart(balanceState.value, incomeState.value, withdrawalState.value)
                ChartLegend()
                RecentTransactions(db)
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
fun CircularChart(balance: String, income: String, withdrawal: String) {
    val totalBalance = balance.toFloatOrNull() ?: 0.0f
    val totalIncome = income.toFloatOrNull() ?: 0.0f
    val totalWithdrawal = withdrawal.toFloatOrNull() ?: 0.0f

    val depositPercentage = if (totalBalance > 0) (totalIncome / totalBalance) * 360 else 0f
    val withdrawPercentage = if (totalBalance > 0) (totalWithdrawal / totalBalance) * 360 else 0f

    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(vertical = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20.dp.toPx()
            val size = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            drawArc(
                color = Color(0XFF1E7832),
                startAngle = 270f,
                sweepAngle = 0.7f * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = Color.Red,
                startAngle = 270f + 0.7f * 360f,
                sweepAngle = 0.3f * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}



@Composable
fun ChartLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = Color(0XFF1E7832), label = "Deposit")
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(color = Color.Red, label = "Withdraw")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun TransactionDetailRecentTransactions(transactionList: List<Transactions>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF1A0E8C), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("Recent Transactions:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        transactionList.forEach { transaction ->
            TransactionDetailItem(
                transactionName = transaction.transactionName,
                amount = transaction.amount.toString(),
                isDeposit = transaction.isDeposit,
                dateTime = transaction.dateTime
            )
        }
    }
}


@Composable
fun TransactionDetailItem(transactionName: String, amount: String, isDeposit: Boolean, dateTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(if (isDeposit) R.drawable.ic_withdraw else R.drawable.ic_send),
            contentDescription = if (isDeposit) "Deposit" else "Withdraw",
            tint = if (isDeposit) Color(0XFF1E7832) else Color.Red,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = transactionName, fontSize = 16.sp, color = Color.White)
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(
                text = if (isDeposit) "+$amount" else "-$amount",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold
            )
            Text(text = dateTime, fontSize = 12.sp, color = Color.White)
        }
    }
}



data class Transactions(
    val transactionName: String,
    val amount: Int,
    val isDeposit: Boolean,
    val dateTime: String
)

private const val TAG = "TransactionsDetails"