package com.jonesandjay123.geminipicturereader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PictureRecognizeScreen() {
    var language by remember { mutableStateOf("EN") }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val stringResources = StringResources(context)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 標題和語言選擇下拉選單
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 使用 `stringResources.getString` 根據選擇的語言顯示文字
            Text(
                text = stringResources.getString(R.string.title, language),
                style = MaterialTheme.typography.titleLarge
            )

            // 語言選擇下拉選單
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { expanded = true }) {
                    Text(text = stringResources.getString(if (language == "EN") R.string.en else R.string.zh, language))
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            language = "EN"
                            expanded = false
                        },
                        text = { Text(stringResources.getString(R.string.en, language)) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            language = "中文"
                            expanded = false
                        },
                        text = { Text(stringResources.getString(R.string.zh, language)) }
                    )
                }
            }
        }

        // LazyRow 模擬（暫時只放置簡單的文字）
        Text(
            text = stringResources.getString(R.string.image_selection_placeholder, language),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        // Row 模擬（暫時只放置簡單的文字）
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResources.getString(R.string.input_field_placeholder, language),
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
            )

            Text(
                text = stringResources.getString(R.string.button_placeholder, language),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        // 結果顯示區域
        Text(
            text = stringResources.getString(R.string.result_placeholder, language),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .fillMaxSize()
        )
    }
}
