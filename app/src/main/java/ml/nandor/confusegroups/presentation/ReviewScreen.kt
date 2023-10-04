package ml.nandor.confusegroups.presentation

import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ml.nandor.confusegroups.R
import okio.utf8Size
import timber.log.Timber

@Composable
fun ReviewScreen(viewModel: MainViewModel, playNoise: (Boolean) -> Int) {

    val colorGood = Color.Green.copy(alpha=0.1f)
    val colorBad = Color.Red.copy(alpha=0.1f)
    val colorNeutral = Color.Transparent
    val selectedColor = remember { mutableStateOf(colorNeutral) }
    val backGroundColor = animateColorAsState(
        targetValue = selectedColor.value,
        animationSpec = tween(100, 0, )
    )

    val provideUserFeedback = {success:Boolean ->

        if (success){
            selectedColor.value = colorGood
        } else {
            selectedColor.value = colorBad
        }
        GlobalScope.launch {
            delay( if(success) 300 else 3000 )
            selectedColor.value = colorNeutral
        }

        playNoise(success)
    }

    BackHandler(onBack = {
        viewModel.selectDeck(null)
    })

    Column(
        Modifier.background(
            color=backGroundColor.value
        )
    ) {

        val question = viewModel.currentQuestion.value
        val correctness = viewModel.cardCorrectness.value

        Row(){
            Text(text = "Level: "+viewModel.deckLevel.value.toString())
        }

        CardFront(question.front, viewModel)

        if (question.options.size == 4){
            if (viewModel.comparisonQuestion.value == null){
                Row(
                    modifier = Modifier
                        .weight(1.0f)
                ) {
                    CardBackOption(question.options[0], correctness[0], viewModel, provideUserFeedback)
                    CardBackOption(question.options[1], correctness[1], viewModel, provideUserFeedback)
                }

                Row(
                    modifier = Modifier
                        .weight(1.0f)
                ) {
                    CardBackOption(question.options[2], correctness[2], viewModel, provideUserFeedback)
                    CardBackOption(question.options[3], correctness[3], viewModel, provideUserFeedback)
                }
            } else {
                CardComparisonBad(text = viewModel.comparisonQuestion.value!!, viewModel = viewModel)
            }

        } else {
            CardOnlyOption(text = question.options[0], viewModel, provideUserFeedback)
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardFront(text:String, viewModel: MainViewModel) {
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1.0f)
            .combinedClickable(
                onClick = {
                    viewModel.addComparisonPopup(text)
                },
                onLongClick = {
                    viewModel.setManualLeft(text)
                }

            )
    ) {
        LargeCardContent(text)
    }
}

@Composable
private fun LargeCardContent(text: String) {
    val imageRegex = "!\\[.*]\\(.*\\)".toRegex()
    
    if (imageRegex.matches(text)) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                contentDescription = "Sample icon",
//                modifier = Modifier.fillMaxSize()
//            )
        val link = text.split("(")[1].split(")")[0] // the exact purpose of a regex
        AsyncImage(
            model = link,
            contentDescription = "Sample Image",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .wrapContentSize(),
            textAlign = TextAlign.Center,
            fontSize = if (text.length < 6) 128.sp else 64.sp
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
        LargeCardContent(text = text)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardComparisonBad(text:String, viewModel: MainViewModel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1.0f)
            .combinedClickable(
                onClick = {
                    viewModel.nextQuestion(false)
                }
            )
    ) {
        LargeCardContent(text = text)
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
                        if (viewModel.isClickable()) {
                            val success = viewModel.checkAnswer(text)
                            playNoise(success)
                        }

                    },
                    onLongClick = {
                        viewModel.displayComparison(text)
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
                .fillMaxHeight(),
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
