package com.beautyspa.app.ui.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beautyspa.app.data.repository.ApiRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(ApiRepository()))
) {
    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current
    val googleSignInClient = getGoogleSignInClient(
        context,
        "955878805230-audoltleqq0ba8ailek0baokjuhf2vo0.apps.googleusercontent.com"
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = GoogleSignInContract(googleSignInClient),
        onResult = { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                viewModel.onGoogleSignInResult(account?.idToken)
            } catch (e: ApiException) {
                viewModel.onGoogleSignInResult(null)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { googleSignInLauncher.launch(Unit) }) {
            Text("Sign in with Google")
        }

        when (val state = loginState) {
            is LoginState.Success -> {
                // Handle successful login, e.g., navigate to another screen
                onLoginSuccess()
            }
            is LoginState.Error -> {
                Text("Error: ${state.message}")
            }
            LoginState.Loading -> {
                Text("Loading...")
            }
            else -> {}
        }
    }
}

private fun getGoogleSignInClient(context: Context, serverClientId: String): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(serverClientId)
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private class GoogleSignInContract(private val googleSignInClient: GoogleSignInClient) :
    ActivityResultContract<Unit, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return googleSignInClient.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }
}
