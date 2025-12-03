package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.compose_multiplatform
@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF00A8A8)) // бирюзовый фон как на скрине
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Белый карточка-контейнер
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Заголовок «Авторизация»
                        Text(
                            text = "Авторизация",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00A8A8),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Подзаголовок
                        Text(
                            text = "Пожалуйста, введите свой номер телефона",
                            fontSize = 16.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Поле ввода телефона
                        var phone by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { newText ->
                                // Ограничиваем только цифры и максимум 9 после +992
                                val filtered = newText.filter { it.isDigit() }
                                if (filtered.length <= 9) {
                                    phone = filtered
                                }
                            },
                            label = { Text("Номер телефона") },
                            leadingIcon = {
                                Text(
                                    text = "+992",
                                    color = Color(0xFF00A8A8),
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF00A8A8),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                cursorColor = Color(0xFF00A8A8)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text(
                                    text = "${phone.length}/9",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Кнопка «Далее»
                        Button(
                            onClick = { /* TODO */ },
                            enabled = phone.length == 9, // активируется только когда 9 цифр
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D4D4),
                                disabledContainerColor = Color(0xFFAAD7D7)
                            )
                        ) {
                            Text(
                                text = "Далее",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}