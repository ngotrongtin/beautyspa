package com.beautyspa.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsState()

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
