package com.example.coinpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coinpay.ui.theme.CoinPayTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinPayTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MyApp(navController)
                }
            }
        }
    }
}

@Composable
fun MyApp(navController: NavController){
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    NavHost(navController as NavHostController, startDestination = "splash_screen") {
        composable("Splash_Screen") {
            AnimatedSplashScreen(navController = navController,context)
        }
        composable("onboarding_screen") {
            OnboardingScreen(context, navController)
        }
        composable("LoginOrSignup"){
            loginSignup(navController)
        }
        composable("Signup"){
            SignupScreen(navController)
        }
        composable("Login"){
            LoginScreen(navController)
        }
        composable("Setup"){
            setupDone(navController)
        }
        composable("Home"){
            HomeScreen(navController,db,"03168701092")
        }
        composable("Transactions"){
            TransactionDetailScreen(navController,context,db)
        }
        composable("Transaction"){
            TransactionScreen(navController,db)
        }


    }
}



