package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.ui.theme.YahtzeeTheme

@Composable
fun Settings(navController: NavController){
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)),
                startY = 0f,
                endY = Float.POSITIVE_INFINITY
    )

    )
    )

}


@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    YahtzeeTheme {
        Settings(navController = rememberNavController())
    }
}

