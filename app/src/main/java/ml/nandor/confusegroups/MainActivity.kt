package ml.nandor.confusegroups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ml.nandor.confusegroups.ui.theme.ConfuseGroupsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfuseGroupsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .aspectRatio(1.0f)
                        ) {
                            Text(
                                text = "犬",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .wrapContentSize(),
                                textAlign = TextAlign.Center,
                                fontSize = 128.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                            .weight(1.0f)
                        ){
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .padding(16.dp)
                                    .fillMaxHeight(),
                                colors = CardDefaults.cardColors(containerColor = Color.Green)
                            ) {
                                Text(
                                    text = "Dog",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontSize = 32.sp)
                            }
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .padding(16.dp)
                                    .fillMaxHeight()
                            ) {
                                Text(                                text = "Big",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 32.sp)
                            }
                        }

                        Row(
                            modifier = Modifier
                            .weight(1.0f)
                        ){
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .padding(16.dp)
                                    .fillMaxHeight()
                            ) {
                                Text(                                text = "Bow",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 32.sp)
                            }
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .padding(16.dp)
                                    .fillMaxHeight(),
                                colors = CardDefaults.cardColors(containerColor = Color.Red)
                            ) {
                                Text(                                text = "Person",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .wrapContentSize(),
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontSize = 32.sp)
                            }
                        }

                    }

                }
            }
        }
    }
}