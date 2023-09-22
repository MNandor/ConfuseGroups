package ml.nandor.confusegroups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.ui.theme.ConfuseGroupsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfuseGroupsTheme {
                val viewModel: MainViewModel = hiltViewModel()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {

                        CardFront()

                        Row(
                            modifier = Modifier
                            .weight(1.0f)
                        ){
                            CardBackOption("Dog", GuessOptionState.GOOD)
                            CardBackOption("Big")
                        }

                        Row(
                            modifier = Modifier
                            .weight(1.0f)
                        ){
                            CardBackOption("Bow")
                            CardBackOption("Person", GuessOptionState.BAD)
                        }

                    }

                }
            }
        }
    }

    @Composable
    private fun CardFront() {
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
    }

    enum class GuessOptionState{
        BASE,
        GOOD,
        BAD
    }

    @Composable
    private fun RowScope.CardBackOption(text: String, state: GuessOptionState = GuessOptionState.BASE) {
        // Base state = color unmodified
        if (state == GuessOptionState.BASE){
            ElevatedCard(
                modifier = Modifier.Companion
                    .weight(1.0f)
                    .padding(16.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
            }
        } else {
            // this code is the duplicate of the base case, except where colors are set
            ElevatedCard(
                modifier = Modifier.Companion
                    .weight(1.0f)
                    .padding(16.dp)
                    .fillMaxHeight(),
                // set color
                colors = CardDefaults.cardColors(containerColor = if (state == GuessOptionState.GOOD) Color.Green else Color.Red)
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                    // set color
                    color = Color.Black,
                    fontSize = 32.sp
                )
            }
        }

    }
}