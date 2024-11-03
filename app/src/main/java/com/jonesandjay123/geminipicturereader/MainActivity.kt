package com.jonesandjay123.geminipicturereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.jonesandjay123.geminipicturereader.ui.theme.GeminiPictureReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiPictureReaderTheme {
                Surface {
                    PictureRecognizeScreen()
                }
            }
        }
    }
}