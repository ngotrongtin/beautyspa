package com.beautyspa.app.ui.screens.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beautyspa.app.BuildConfig
import com.beautyspa.app.ui.screens.sharedViewmodel.BookingStatusViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel = viewModel(),
    bookingStatusViewModel: BookingStatusViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    // Initialize Stripe
    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
    }

    // PaymentSheet using the new API
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            PaymentSheetResult.Completed -> {
                Toast.makeText(context, "Payment successful!", Toast.LENGTH_LONG).show()
                bookingStatusViewModel.complete()
                vm.clearPaymentState()
            }
            PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Payment canceled", Toast.LENGTH_SHORT).show()
                vm.clearPaymentState()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(context, "Payment failed: ${result.error.localizedMessage}", Toast.LENGTH_LONG).show()
                bookingStatusViewModel.fail()
                vm.clearPaymentState()
            }
        }
    }

    // Watch for payment state and launch PaymentSheet automatically
    LaunchedEffect(uiState.paymentState) {
        val state = uiState.paymentState
        if (state?.status == "READY_FOR_PAYMENT" && state.client_secret != null) {
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = state.client_secret,
                configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "Beauty Spa"
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Chat") })
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            reverseLayout = true,
            contentPadding = PaddingValues(bottom = 72.dp) // leave space for input row
        ) {
            items(uiState.messages.reversed()) { msg ->
                val bg = if (msg.isUser) Color(0xFFDFF6FF) else Color(0xFFF1F1F1)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(bg)
                        .padding(12.dp)
                ) {
                    Text(text = msg.text)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //.navigationBarsPadding()
                .imePadding()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask something...") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val q = text.text.trim()
                    if (q.isNotEmpty()) {
                        vm.ask(q)
                        text = TextFieldValue("")
                    }
                },
                enabled = !uiState.loading
            ) { Text("Send") }
        }
    }
}
