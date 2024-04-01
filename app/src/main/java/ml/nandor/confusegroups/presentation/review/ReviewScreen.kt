package ml.nandor.confusegroups.presentation.review

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.VectorDrawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ml.nandor.confusegroups.R
import ml.nandor.confusegroups.presentation.AutoResizeText
import ml.nandor.confusegroups.presentation.FontSizeRange
import ml.nandor.confusegroups.presentation.MainViewModel
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import timber.log.Timber
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

@Composable
fun ReviewScreen(playNoise: (Boolean) -> Int, commonViewModel: CommonViewModel) {

    val localViewModel:ReviewViewModel = hiltViewModel()

    commonViewModel.selectedDeck.value?.let { localViewModel.beginReviewingADeck(it) }

    val colorGood = Color.Green.copy(alpha = 0.1f)
    val colorBad = Color.Red.copy(alpha = 0.1f)
    val colorNeutral = Color.Transparent
    val selectedColor = remember { mutableStateOf(colorNeutral) }
    val backGroundColor = animateColorAsState(
        targetValue = selectedColor.value,
        animationSpec = tween(100, 0)
    )

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
    })

    val card = localViewModel.currentQuestion.value

    if (card == null)
        return

    val correctness = localViewModel.cardCorrectness.value

    val provideUserFeedback = { success: Boolean ->

        if (success) {
            selectedColor.value = colorGood
        } else {
            selectedColor.value = colorBad
        }
        GlobalScope.launch {
            delay(if (success) 300 else 3000)
            selectedColor.value = colorNeutral
        }

        playNoise(success)
    }



    if (isPortrait) {
        Column(
            Modifier.background(
                color = backGroundColor.value
            )
        ) {

            Row(

            ) {
                Text(text = "Level: " + localViewModel.deckLevel.value.toString())
            }

            CardFront(card.note.id,  card.note.question?:"missing front", commonViewModel)

            if (card.options.size == 4) {
                if (localViewModel.comparisonQuestion.value == null) {
                    Row(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        CardBackOption(
                            card.options[0],
                            correctness[0],
                            localViewModel,
                            provideUserFeedback
                        )
                        CardBackOption(
                            card.options[1],
                            correctness[1],
                            localViewModel,
                            provideUserFeedback
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        CardBackOption(
                            card.options[2],
                            correctness[2],
                            localViewModel,
                            provideUserFeedback
                        )
                        CardBackOption(
                            card.options[3],
                            correctness[3],
                            localViewModel,
                            provideUserFeedback
                        )
                    }
                } else {
                    CardComparisonBad(
                        text = localViewModel.comparisonQuestion.value!!,
                        viewModel = localViewModel,
                        commonViewModel = commonViewModel
                    )
                }

            } else {
                CardOnlyOption(text = card.options[0], localViewModel, provideUserFeedback)
            }

        }
    } else {
        Row(
            Modifier.background(
                color = backGroundColor.value
            )
        ) {

            Row(

            ) {
                Text(text = "Level: " + localViewModel.deckLevel.value.toString())
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.0f)
            ) {
                CardFront(card.note.id, card.note.question?:"missing front", commonViewModel)
            }

            if (card.options.size == 4) {
                if (localViewModel.comparisonQuestion.value == null) {
                    Column(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        CardOptionWrapper(
                            card.options[0],
                            correctness[0],
                            localViewModel,
                            provideUserFeedback
                        )
                        CardOptionWrapper(
                            card.options[1],
                            correctness[1],
                            localViewModel,
                            provideUserFeedback
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        CardOptionWrapper(
                            card.options[2],
                            correctness[2],
                            localViewModel,
                            provideUserFeedback
                        )
                        CardOptionWrapper(
                            card.options[3],
                            correctness[3],
                            localViewModel,
                            provideUserFeedback
                        )
                    }
                } else {
                    CardComparisonBad(
                        text = localViewModel.comparisonQuestion.value!!,
                        viewModel = localViewModel,
                        commonViewModel = commonViewModel
                    )
                }

            } else {
                CardOnlyOption(text = card.options[0], localViewModel, provideUserFeedback)
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardFront(text: String, displayText: String, commonViewModel: CommonViewModel) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(1.0f)
            .combinedClickable(
                onClick = {
//                    viewModel.addComparisonPopup(text)
                },
                onLongClick = {
                    commonViewModel.setManualLeft(text)
                }

            )
    ) {
        Surface() {
            // number of groups current card is a member of
            // todo add to group popup needs to be opened at least once
            Text(commonViewModel.getGroupMembershipCount(text).toString())
            CardContent(displayText)
        }
    }
}


@Composable
private fun CardContent(text: String, color: Color = Color.Unspecified, isSmall: Boolean = false) {

    val pattern = "\\!\\[(.*?)\\]\\((.*?)\\)"
    val matcher: Matcher = Pattern.compile(pattern).matcher(text)

    val sizeRange = if (isSmall) {
        FontSizeRange(16.sp, 32.sp)
    } else {
        if (text.length < 5)
            FontSizeRange(64.sp, 128.sp)
        else
            FontSizeRange(32.sp, 64.sp)
    }

    if (matcher.find()) {
        val firstMatch = matcher.group(1) //local files
        val secondMatch = matcher.group(2) //url

        val file = File("/storage/emulated/0/ConfuseGroups/" + firstMatch)

        if (!firstMatch.isNullOrEmpty() && file.isFile) {

            Timber.d("${file}, ${file.isFile}, ${file.path}")

            val bmp = BitmapFactory.decodeFile(file.path)

            if (bmp != null){
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Sample Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (firstMatch.endsWith(".svg")){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(file)
                        .decoderFactory(SvgDecoder.Factory())
                        .error(R.drawable.ic_launcher_foreground)
                        .placeholder(R.drawable.ic_launcher_background)
                        .build(),
                    contentDescription = "Sample Image",
                    modifier = Modifier.fillMaxSize()
                )
            }

        } else if (!secondMatch.isNullOrEmpty()) {
            AsyncImage(
                model = secondMatch,
                contentDescription = "Sample Image",
                modifier = Modifier.fillMaxSize()
            )
        }

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
private fun CardOnlyOption(text: String, viewModel: ReviewViewModel, playNoise: (Boolean) -> Int) {
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
private fun CardComparisonBad(text: String, viewModel: ReviewViewModel, commonViewModel: CommonViewModel) {
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
//                    commonViewModel.setManualLeft()
                    // todo to make this work, use ID, not question
                }
            )
    ) {
        CardContent(text = text)
    }
}

@Composable
private fun ColumnScope.CardOptionWrapper(
    text: String,
    state: ReviewViewModel.CardCorrectness = ReviewViewModel.CardCorrectness.BASE,
    viewModel: ReviewViewModel,
    playNoise: (Boolean) -> Int
) {
    Row(
        modifier = Modifier.weight(1.0f)
    ) {
        CardBackOption(text, state, viewModel, playNoise)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.CardBackOption(
    text: String,
    state: ReviewViewModel.CardCorrectness = ReviewViewModel.CardCorrectness.BASE,
    viewModel: ReviewViewModel,
    playNoise: (Boolean) -> Int
) {
    // Base state = color unmodified
    if (state == ReviewViewModel.CardCorrectness.BASE) {
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
            colors = CardDefaults.cardColors(containerColor = if (state == ReviewViewModel.CardCorrectness.GOOD) Color.Green else Color.Red)
        ) {
            CardContent(text = text, color = Color.Black, isSmall = true)
        }
    }

}
