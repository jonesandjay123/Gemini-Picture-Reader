package com.jonesandjay123.geminipicturereader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PictureRecognizeScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 標題
        Text(
            text = "Picture Recognize Screen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // LazyRow 模擬（暫時只放置簡單的文字）
        Text(
            text = "Image Selection Placeholder",
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
                text = "Input Field Placeholder",
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
            )

            Text(
                text = "Button Placeholder",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        // 結果顯示區域
        Text(
            text = "Result Placeholder",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .fillMaxSize()
        )
    }
}
