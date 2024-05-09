package com.kahana.cleansky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import com.kahana.cleansky.network.AirQualityApiImp
import com.kahana.cleansky.network.AirQualityDataSource
import com.kahana.cleansky.ui.theme.CleanSkyTheme
import com.kahana.cleansky.viewmodel.AirQualityViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by lazy {
        AirQualityViewModel(AirQualityApiImp( AirQualityDataSource.apiService))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.airQualityData.observe(this, Observer {
            val data = it
        })

        val resp = viewModel.getAirQualityData()

        setContent {
            CleanSkyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanSkyTheme {
        Greeting("Android")
    }
}