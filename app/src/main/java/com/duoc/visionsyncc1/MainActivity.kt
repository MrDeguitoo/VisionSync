package com.duoc.visionsyncc1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.visionsyncc1.navigation.AppNavigation
import com.duoc.visionsyncc1.ui.theme.VisionSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisionSyncTheme {
                AppNavigation()
            }
        }
    }
}