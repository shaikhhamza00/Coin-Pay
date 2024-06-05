package com.example.coinpay

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.coinpay.ui.theme.CoinPayTheme
import kotlinx.coroutines.delay

class AnimatedSplashScreen : ComponentActivity() {
}
fun isFirstLaunch(context: Context): Boolean {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val isFirstLaunch = sharedPreferences.getBoolean("FIRST_LAUNCH", true)
    if (isFirstLaunch) {
        // If it's the first launch, update SharedPreferences to reflect that
        sharedPreferences.edit().putBoolean("FIRST_LAUNCH", false).apply()
    }
    return isFirstLaunch
}
@Composable
fun AnimatedSplashScreen(navController: NavController,context: Context) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        if (isFirstLaunch(context)) {
            navController.popBackStack()
            navController.navigate("onboarding_screen")
        } else {
            navController.popBackStack()
            navController.navigate("LoginOrSignup")
        }
    }
    Splash(alphaAnim.value)
}

@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0XFF2C14DD),
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                alpha = alpha,
                contentDescription = "Green Wallet Logo",
                modifier = Modifier
                    .offset(x = (1 - alpha) * 200.dp) // Adjust the initial offset to start from the right
                    .fillMaxWidth(0.4f)
                    .padding(vertical = 12.dp)
            )
            Text(
                "Your Personal Financial Hub",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}