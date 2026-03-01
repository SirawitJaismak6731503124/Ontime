package com.ontime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ontime.ui.OnTimeNavHost
import com.ontime.ui.theme.OnTimeTheme
import com.ontime.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnTimeTheme {
                val navigationController = rememberNavController()
                val viewModel: SessionViewModel = viewModel()

                OnTimeNavHost(
                    navController = navigationController,
                    viewModel = viewModel
                )
            }
        }
    }
}
