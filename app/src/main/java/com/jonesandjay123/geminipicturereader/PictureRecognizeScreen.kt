package com.jonesandjay123.geminipicturereader

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

@Composable
fun PictureRecognizeScreen(
    viewModel: PictureRecognizeViewModel = viewModel()
) {
    var language by remember { mutableStateOf("EN") }
    var expanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
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

    // 初始化 TTS 引擎
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                // 處理初始化失敗
            }
        }
    }

    // 設定 UtteranceProgressListener
    DisposableEffect(tts) {
        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // 可以在這裡處理朗讀開始時的邏輯
            }

            override fun onDone(utteranceId: String?) {
                // 確保在主線程更新狀態
                Handler(Looper.getMainLooper()).post {
                    isPlaying = false
                }
            }

            override fun onError(utteranceId: String?) {
                // 處理朗讀錯誤
                Handler(Looper.getMainLooper()).post {
                    isPlaying = false
                }
            }
        }
        tts.setOnUtteranceProgressListener(listener)
        onDispose {
            tts.setOnUtteranceProgressListener(null)
        }
    }

    // 切換語言
    LaunchedEffect(language) {
        tts.language = when (language) {
            "EN" -> Locale.ENGLISH
            "中文" -> Locale.CHINESE
            else -> Locale.ENGLISH
        }
    }

    // 停止 TTS 朗讀
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // 觀察 ViewModel 的 UI 狀態
    val uiState by viewModel.uiState.collectAsState()

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
                    isPlaying = false // 清除圖片時停止播放狀態
                    tts.stop() // 停止 TTS
                    // 重置 UI 狀態
                    viewModel.resetUiState()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResources.getString(R.string.image_remove_button, language))
            }
        }

        // 結果顯示區域（當圖片顯示時才顯示）
        if (imageBitmap != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            // 觸發圖片識別邏輯
                            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
                            viewModel.recognizeImage(bitmap)
                        },
                        enabled = uiState !is UiState.Loading
                    ) {
                        Text(text = stringResources.getString(R.string.recognition_button, language))
                    }

                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                tts.stop()
                                isPlaying = false
                            } else {
                                val textToSpeak = when (uiState) {
                                    is UiState.Success -> (uiState as UiState.Success).outputText
                                    else -> stringResources.getString(R.string.output_sentence, language)
                                }
                                val utteranceId = UUID.randomUUID().toString()
                                tts.speak(
                                    textToSpeak,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    utteranceId
                                )
                                isPlaying = true
                            }
                        },
                        enabled = uiState is UiState.Success
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Clear else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Stop Audio" else "Play Audio"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    is UiState.Success -> {
                        Text(
                            text = (uiState as UiState.Success).outputText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = (uiState as UiState.Error).errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    else -> {
                        // Initial state or other states
                        Text(
                            text = stringResources.getString(R.string.output_sentence, language),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
