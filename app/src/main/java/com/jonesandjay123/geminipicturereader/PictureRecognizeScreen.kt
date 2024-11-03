package com.jonesandjay123.geminipicturereader

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PictureRecognizeScreen() {
    var language by remember { mutableStateOf("EN") }
    var expanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current
    val stringResources = StringResources(context)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageBitmap = bitmap?.asImageBitmap()
        }
    }

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
            Text(
                text = stringResources.getString(R.string.title, language),
                style = MaterialTheme.typography.titleLarge
            )

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

        // 圖片選擇區域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = stringResources.getString(R.string.image_selection_placeholder, language),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = stringResources.getString(R.string.image_selection_placeholder, language))
                }
            }
        }

        // 移除圖片按鈕（當圖片顯示時才出現）
        if (imageBitmap != null) {
            Button(
                onClick = {
                    imageUri = null
                    imageBitmap = null
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResources.getString(R.string.image_remove_button, language))
            }
        }

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
