package com.example.latian2.screen.pages

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.latian2.R
import com.example.latian2.model.AuthState
import com.example.latian2.model.AuthViewModel
import com.example.latian2.screen.parts.AlternativeLogin
import com.example.latian2.screen.parts.LoginForm
import com.example.latian2.screen.parts.Logo

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val vintageFont = FontFamily(
        Font(R.font.playfull, FontWeight.Normal) // Ganti 'playfull' dengan nama file font Anda
    )

    // Launcher for Google Sign-In
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                Log.d("LoginPage", "Google Sign-In result received")
                authViewModel.handleGoogleSignInResult(it)
            }
        } else {
            Log.e("LoginPage", "Google Sign-In failed with result code: ${result.resultCode}")
        }
    }

    // Handle auth state changes
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    // UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6BF6C3), Color(0xFF3D79F8))
                )),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF6BF6C3), Color(0xFF3D79F8))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Logo()

            Spacer(modifier = Modifier.height(24.dp)) // Tambahkan spacer jika ingin jarak antar elemen
            Text(
                text = "Login",
                fontSize = 36.sp,  // Ukuran font yang lebih besar
                fontWeight = FontWeight.Bold,  // Membuat teks lebih tebal
                fontFamily = vintageFont,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)  // Memberikan jarak ke elemen di bawahnya
            )

            // LoginForm, pastikan tidak memiliki padding yang berlebihan di dalam komponennya
            LoginForm(
                onLoginClick = { identifier, password ->
                    authViewModel.login(identifier, password)
                },
                onSignupClick = {
                    navController.navigate("signup")
                },
                onGoogleSignIn = {
                    Log.d("LoginPage", "Google Sign-In button clicked")
                    authViewModel.beginGoogleSignIn(
                        onSuccess = { intentSender ->
                            signInLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        },
                        onFailure = { exception ->
                            Toast.makeText(context, "Google Sign-In Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }
    }
}
