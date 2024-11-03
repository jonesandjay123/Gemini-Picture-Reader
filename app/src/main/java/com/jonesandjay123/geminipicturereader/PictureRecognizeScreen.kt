package com.jonesandjay123.geminipicturereader

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var language by remember { mutableStateOf("中文") } // 預設語言改為中文
    var languageExpanded by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val stringResources = StringResources(context)

    // 根據語言載入類別選項
    val categoryOptions = if (language == "EN") {
        context.resources.getStringArray(R.array.category_options_en).toList()
    } else {
        context.resources.getStringArray(R.array.category_options_zh).toList()
    }

    // 設置默認類別
    LaunchedEffect(language) {
        category = categoryOptions.firstOrNull() ?: ""
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            viewModel.resetUiState()
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
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post {
                    isPlaying = false
                }
            }

            override fun onError(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post {
                    isPlaying = false
                }
            }
        }
        tts.setOnUtteranceProgressListener(listener)
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // 根據選擇的語言設置 TTS 語言
    LaunchedEffect(language) {
        tts.language = when (language) {
            "EN" -> Locale.ENGLISH
            "中文" -> Locale.TRADITIONAL_CHINESE
            else -> Locale.TRADITIONAL_CHINESE
        }
    }

    // 觀察 ViewModel 的 UI 狀態
    val uiState by viewModel.uiState.collectAsState()

    // 自動播放 TTS
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success && !isPlaying) {
            val textToSpeak = (uiState as UiState.Success).outputText
            val utteranceId = UUID.randomUUID().toString()
            tts.speak(
                textToSpeak,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
            isPlaying = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 標題和選單
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResources.getString(R.string.title, language),
                style = MaterialTheme.typography.titleLarge
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 類別選擇
                Box {
                    TextButton(onClick = { categoryExpanded = true }) {
                        Text(text = if (category.isNotEmpty()) category else stringResources.getString(R.string.select_category, language))
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryOptions.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                },
                                text = { Text(option) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 語言選擇
                Box {
                    TextButton(onClick = { languageExpanded = true }) {
                        Text(text = language)
                    }

                    DropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        listOf("中文", "EN").forEach { lang ->
                            DropdownMenuItem(
                                onClick = {
                                    language = lang
                                    languageExpanded = false
                                },
                                text = { Text(lang) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 圖片選擇區域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val bitmap = remember(imageUri) {
                    val inputStream = context.contentResolver.openInputStream(imageUri!!)
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = stringResources.getString(R.string.image_selection_placeholder, language))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 移除圖片按鈕
        if (imageUri != null) {
            Button(
                onClick = {
                    imageUri = null
                    isPlaying = false
                    tts.stop()
                    viewModel.resetUiState()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResources.getString(R.string.image_remove_button, language))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 結果顯示區域
        if (imageUri != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val prompt = viewModel.getPrompt(language, category)
                            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
                            viewModel.recognizeImage(bitmap, prompt)
                        },
                        enabled = uiState !is UiState.Loading
                    ) {
                        Text(text = viewModel.getButtonText(language, category))
                    }

                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                tts.stop()
                                isPlaying = false
                            } else {
                                val textToSpeak = when (uiState) {
                                    is UiState.Success -> (uiState as UiState.Success).outputText
                                    else -> ""
                                }
                                if (textToSpeak.isNotEmpty()) {
                                    val utteranceId = UUID.randomUUID().toString()
                                    tts.speak(
                                        textToSpeak,
                                        TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        utteranceId
                                    )
                                    isPlaying = true
                                }
                            }
                        },
                        enabled = uiState is UiState.Success
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Stop Audio" else "Play Audio"
                        )
                    }

                    // 複製按鈕
                    IconButton(
                        onClick = {
                            val textToCopy = when (uiState) {
                                is UiState.Success -> (uiState as UiState.Success).outputText
                                else -> ""
                            }
                            if (textToCopy.isNotEmpty()) {
                                copyToClipboard(context, textToCopy)
                                Toast.makeText(context, stringResources.getString(R.string.copy_success, language), Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, stringResources.getString(R.string.copy_fail, language), Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = uiState is UiState.Success
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResources.getString(R.string.copy_button_description, language)
                        )
                    }



                }

                Spacer(modifier = Modifier.height(16.dp))

                // 顯示結果
                when (uiState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is UiState.Success -> {
                        Text(
                            text = (uiState as UiState.Success).outputText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = (uiState as UiState.Error).errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}


// 複製到剪貼板的函數
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}