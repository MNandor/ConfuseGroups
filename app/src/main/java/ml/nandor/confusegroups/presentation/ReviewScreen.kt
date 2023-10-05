package ml.nandor.confusegroups.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        Surface() {
            Text(viewModel.getManualRelationsCount(text).toString())
            CardContent(text)
        }
    }
}


@Composable
private fun CardContent(text:String, color:Color = Color.Unspecified, isSmall:Boolean = false){

    val imageRegex = "!\\[.*]\\(.*\\)".toRegex()

    val sizeRange = if (isSmall){
        FontSizeRange(16.sp, 32.sp)
    } else {
        if (text.length < 5)
            FontSizeRange(64.sp, 128.sp)
        else
            FontSizeRange(32.sp, 64.sp)
    }

    if (imageRegex.matches(text)) {

        val link = text.split("(")[1].split(")")[0] // the exact purpose of a regex
        AsyncImage(
            model = link,
            contentDescription = "Sample Image",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        AutoResizeText(
            text = text,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .wrapContentSize(),
            textAlign = TextAlign.Center,
            color = color,
            fontSizeRange = sizeRange,
            maxLines = 5,
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
        CardContent(text = text)
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
                },
                onLongClick = {
                    viewModel.setManualLeft(text)
                }
            )
    ) {
        CardContent(text = text)
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
            CardContent(text = text, isSmall = true)
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
            CardContent(text = text, color = Color.Black, isSmall = true)
        }
    }

}
