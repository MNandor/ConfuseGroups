package ml.nandor.confusegroups.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReviewScreen(viewModel: MainViewModel, playNoise: (Boolean) -> Int) {
    BackHandler(onBack = {
        viewModel.selectDeck(null)
    })

    Column() {

        val question = viewModel.currentQuestion.value
        val correctness = viewModel.cardCorrectness.value

        Row(){
            Text(text = "Level: "+viewModel.deckLevel.value.toString())
        }

        CardFront(question.front)

        if (question.options.size == 4){
            Row(
                modifier = Modifier
                    .weight(1.0f)
            ) {
                CardBackOption(question.options[0], correctness[0], viewModel, playNoise)
                CardBackOption(question.options[1], correctness[1], viewModel, playNoise)
            }

            Row(
                modifier = Modifier
                    .weight(1.0f)
            ) {
                CardBackOption(question.options[2], correctness[2], viewModel, playNoise)
                CardBackOption(question.options[3], correctness[3], viewModel, playNoise)
            }
        } else {
            CardOnlyOption(text = question.options[0], viewModel, playNoise)
        }

    }
}

@Composable
private fun CardFront(text:String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1.0f)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .wrapContentSize(),
            textAlign = TextAlign.Center,
            fontSize = 128.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardOnlyOption(text:String, viewModel: MainViewModel, playNoise: (Boolean) -> Int) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1.0f)
            .combinedClickable(
                onClick = {
                    val success = viewModel.checkAnswer(text)
                    playNoise(success)
                }
            )
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .wrapContentSize(),
            textAlign = TextAlign.Center,
            fontSize = 128.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.CardBackOption(
    text: String,
    state: MainViewModel.CardCorrectness = MainViewModel.CardCorrectness.BASE,
    viewModel: MainViewModel,
    playNoise: (Boolean) -> Int
) {
    // Base state = color unmodified
    if (state == MainViewModel.CardCorrectness.BASE){
        ElevatedCard(
            modifier = Modifier
                .weight(1.0f)
                .padding(16.dp)
                .fillMaxHeight()
                .combinedClickable(
                    onClick = {
                        val success = viewModel.checkAnswer(text)
                        playNoise(success)
                    }
                )
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
            modifier = Modifier
                .weight(1.0f)
                .padding(16.dp)
                .fillMaxHeight()
                .combinedClickable(
                    onClick = {
                        viewModel.checkAnswer(text)
                    }
                ),
            // set color
            colors = CardDefaults.cardColors(containerColor = if (state == MainViewModel.CardCorrectness.GOOD) Color.Green else Color.Red)
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
