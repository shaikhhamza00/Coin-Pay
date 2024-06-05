package com.example.coinpay

import android.content.Context
import android.preference.PreferenceManager
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.NavController
import kotlin.math.absoluteValue

class OnboardingScreen : ComponentActivity() {

}




@Composable
fun OnboardingScreen(
    context: Context,
    navController: NavController
) {
    var currentPage by remember { mutableStateOf(0) }
    val imageResources = listOf(
        R.drawable.trust,
        R.drawable.send_money,
        R.drawable.recieve_money
    )

    val animateTargetPage by animateIntAsState(targetValue = currentPage)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val gestureDetector = remember {
            GestureDetectorCompat(
                context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent): Boolean {
                        return true
                    }

                    override fun onScroll(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean {
                        if (distanceX > 0 && currentPage > 0) {
                            currentPage--
                        } else if (distanceX < 0 && currentPage < imageResources.size - 1) {
                            currentPage++
                        }
                        return true
                    }
                }
            )
        }
        val touchEvent = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    if (dragAmount.x.absoluteValue > 80) {
                        if (dragAmount.x > 0 && currentPage > 0) {
                            currentPage--
                        } else if (dragAmount.x < 0 && currentPage < imageResources.size - 1) {
                            currentPage++
                        }
                        change.consumeAllChanges()
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Set background to white
                .then(touchEvent)
                .padding(12.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BackButton(onBack = {
                        if (currentPage > 0) {
                            currentPage--
                        }
                    })
                    Spacer(modifier = Modifier.weight(1f))
                    SkipButton(onSkip = {
                        navController.navigate("LoginOrSignup")
                    })
                }
            }
            Image(
                painter = painterResource(id = imageResources[animateTargetPage]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            )
        }
    }
}

@Composable
fun SkipButton(onSkip: () -> Unit) {
    Button(
        onClick = onSkip,
        modifier = Modifier.sizeIn(minHeight = 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0XFF2C14DD)
        )

    ) {
        Text(text = "Skip")
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(
        onClick = onBack,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back"
        )
    }
}


@Composable
fun loginSignup(navController: NavController){
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ls), contentDescription = "Login Or Signup", modifier = Modifier.weight(3f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {navController.navigate("Signup")},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFF2C14DD),
                contentColor = Color.White
            )
        ) {
            Text(text = "Sign up")
        }

        // Modified OutlinedButton with outline color
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                      navController.navigate("Login")
            },
            border = BorderStroke(2.dp, Color(0XFF2C14DD)), // Added border with desired color
            colors = ButtonDefaults.buttonColors(
                contentColor = Color(0XFF2C14DD),
                containerColor = Color.White
            )
        ) {
            Text(text = "Login")
        }

        // Added text with different colors using AnnotatedString
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(color = Color(0XFF6F6F6F)))
                append("By continuing you accept our ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0XFF2C14DD)))
                append("Terms of Services")
                pop()
                pushStyle(SpanStyle(color = Color.Black))
                append(" and ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0XFF2C14DD)))
                append("Privacy Policy")
                pop()
                toAnnotatedString()
            },
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            textAlign = TextAlign.Center)
    }
}
