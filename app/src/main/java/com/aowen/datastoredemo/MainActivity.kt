package com.aowen.datastoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aowen.datastoredemo.data.UserPreferences
import com.aowen.datastoredemo.ui.theme.DatastoreDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val userPreferences by mainViewModel.userPreferences.collectAsState(UserPreferences())
            val uiState by mainViewModel.uiState.collectAsState()

            var expanded by remember {
                mutableStateOf(false)
            }

            val colors = mapOf(
                "Red" to Color.Red,
                "Green" to Color.Green,
                "Blue" to Color.Blue,
            )
            DatastoreDemoTheme {
                Scaffold(
                    modifier = Modifier
                        .background(Color(userPreferences.backgroundColor))
                        .fillMaxSize(),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(userPreferences.backgroundColor)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            name = userPreferences.userName,
                            modifier = Modifier.padding(innerPadding)
                        )
                        TextField(
                            value = uiState.usernameField,
                            onValueChange = mainViewModel::updateUserName
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                value = getColorString(uiState.selectedColor),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }
                            ) {
                                colors.forEach { (name, color) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = name)
                                        },
                                        onClick = {
                                            mainViewModel.updateBackgroundColor(color)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        ElevatedButton(onClick = mainViewModel::updatePreferences) {
                            Text(text = "Save Preferences")
                        }
                    }
                }
            }
        }
    }
}

private val colorNameMap = mapOf(
    Color.Red.value to "Red",
    Color.Green.value to "Green",
    Color.Blue.value to "Blue"
)

private fun getColorString(color: Color): String {
    return colorNameMap[color.value] ?: "Unknown"
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello, $name!",
        fontSize = 36.sp,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DatastoreDemoTheme {
        Greeting("Android")
    }
}