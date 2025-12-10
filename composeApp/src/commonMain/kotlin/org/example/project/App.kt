package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.currentOrThrow
import ecooil_kmp.composeapp.generated.resources.Res
import ecooil_kmp.composeapp.generated.resources.ecooil_text
import ecooil_kmp.composeapp.generated.resources.sms
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.networking.InsultCensorClient
import org.example.util.onError
import org.example.util.onSuccess
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun AppContent(client: InsultCensorClient?) {
    val navigator = cafe.adriel.voyager.navigator.LocalNavigator.currentOrThrow

    val scope = rememberCoroutineScope()

    // Состояния
    var phone by remember { mutableStateOf("") }
    var savedPhoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSmsStep by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(60) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00A8A8))
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 5.dp, end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ecooil_text),
                            contentDescription = "Логотип EcoOil",
                            modifier = Modifier
                                .height(120.dp)
                                .padding(bottom = 10.dp)
                        )

                        Text(
                            text = if (!isSmsStep) "Авторизация" else "Пожалуйста, введите код, отправленный на ваш",
                            fontSize = if (!isSmsStep) 24.sp else 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = if (!isSmsStep) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isSmsStep) Color(0xFF00A8A8) else Color.Black,
                            modifier = Modifier.padding(bottom = 32.dp)

                        )

                        Text(
                            text = if (!isSmsStep)
                                "Пожалуйста, введите свой номер телефона"
                            else
                                "Номер мобильного телефона +992 $savedPhoneNumber",
                            fontSize = 14.sp,
                            color = Color(0xFF00A8A8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Поле ввода (номер или код)
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { newText ->
                                val filtered = newText.filter { it.isDigit() }
                                if (!isSmsStep) {
                                    // номер — до 9 цифр
                                    if (filtered.length <= 9) phone = filtered
                                } else {
                                    // код — до 4 цифр
                                    if (filtered.length <= 4) phone = filtered
                                }
                            },
                            label = {
                                Text(
                                    if (!isSmsStep)
                                        "Номер телефона"
                                    else
                                        "Введите SMS код "
                                )

                            },
                            placeholder = {
                                if (isSmsStep) {
                                    Text("XXXX", color = Color.Gray)
                                }
                            },
                            leadingIcon = {
                                if (!isSmsStep) {
                                    Text(
                                        "+992",
                                        color = Color(0xFF00A8A8),
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(Res.drawable.sms),
                                        contentDescription = "SMS",
                                        tint = Color(0xFF00A8A8),
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(24.dp)

                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (!isSmsStep) KeyboardType.Phone else KeyboardType.Number
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF00A8A8),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                cursorColor = Color(0xFF00A8A8)
                            ),
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                            supportingText = {
                                if (!isSmsStep) {
                                    Text(
                                        text = "${phone.length}/9",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    // таймер / повторная отправка
                                    if (timer > 0) {
                                        Text(
                                            text = "Отправить повторно через $timer сек",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Отправить код повторно",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp)
                                                .clickable(enabled = !isLoading) {
                                                    // При клике — имитируем повторную отправку и рестарт таймера
                                                    scope.launch {
                                                        if (isLoading) return@launch
                                                        isLoading = true
                                                        // здесь можно вызвать API повторной отправки
                                                        // имитируем задержку
                                                        delay(800)
                                                        isLoading = false

                                                        timer = 60
                                                        while (timer > 0) {
                                                            delay(1000)
                                                            timer--
                                                        }
                                                    }
                                                },
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF00A8A8),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Кнопка Далее / Подтвердить
                        Button(
                            onClick = {
                                if (!isSmsStep) {
                                    // отправка номера
                                    scope.launch {
                                        if (isLoading) return@launch
                                        isLoading = true

                                        // если номер короче 9 символов — не отправляем
                                        if (phone.length != 9) {
                                            // можно показать ошибку — но пока просто вернем isLoading = false
                                            isLoading = false
                                            return@launch
                                        }

                                        client?.censorWords(phone)
                                            ?.onSuccess {
                                                isLoading = false
                                                savedPhoneNumber = phone
                                                phone = ""
                                                isSmsStep = true

                                                // старт таймера
                                                scope.launch {
                                                    timer = 60
                                                    while (timer > 0) {
                                                        delay(1000)
                                                        timer--
                                                    }
                                                }
                                            }
                                            ?.onError {
                                                // обработка ошибки
                                                isLoading = false
                                            } ?: run {
                                            // если client == null — просто переключаемся (для preview)
                                            isLoading = false
                                            savedPhoneNumber = phone
                                            phone = ""
                                            isSmsStep = true
                                            scope.launch {
                                                timer = 60
                                                while (timer > 0) {
                                                    delay(1000)
                                                    timer--
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // подтверждение SMS кода
                                    // здесь логика отправки кода для проверки
                                    // например: verifyCode(phone)
                                    navigator.replace(MainRootScreen)
                                    println("Отправляем/проверяем код: $phone")
                                }
                            },
                            enabled = !isLoading, // кнопка выключена во время загрузки
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D4D4),
                                disabledContainerColor = Color(0xFFAAD7D7)
                            )
                        ) {
                            // Контент кнопки: фиксированная высота контейнера, чтобы не было сдвигов
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp), // фиксированная высота содержимого кнопки
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.5.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = if (!isSmsStep) "Далее" else "Подтвердить",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
