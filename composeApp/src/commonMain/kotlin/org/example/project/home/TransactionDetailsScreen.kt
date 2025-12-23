package org.example.project.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.example.data.TransactionDto
import org.example.data.amountUi
import org.example.data.dateNoSeconds
import org.example.data.payTitle

class TransactionDetailsScreen(
    private val tx: TransactionDto
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Детали операции") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(tx.payTitle(), fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text(tx.amountUi().text)
                Spacer(Modifier.height(8.dp))
                Text(tx.dateNoSeconds())
            }
        }
    }
}