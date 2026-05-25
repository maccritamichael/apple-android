package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.AppDatabase
import com.example.data.repository.AppIconRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.IconExplorerViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize the persistent database and repository layer locally
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = AppIconRepository(database.savedIconDao())

    // Instantiate debounced MVVM ViewModel using the factory provider pattern
    val viewModelCount: IconExplorerViewModel by viewModels {
      IconExplorerViewModel.Factory(repository)
    }

    setContent {
      MyApplicationTheme {
        MainScreen(viewModel = viewModelCount)
      }
    }
  }
}
