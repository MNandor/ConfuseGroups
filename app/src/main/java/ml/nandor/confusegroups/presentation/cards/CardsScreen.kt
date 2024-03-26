package ml.nandor.confusegroups.presentation.cards

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import timber.log.Timber

@Composable
fun CardsScreen(commonViewModel: CommonViewModel){
    Timber.d("Launched")

    val localViewModel: CardsViewModel = hiltViewModel()

    commonViewModel.selectedDeck.value?.let {
        Timber.d("Loading from $it")
        localViewModel.loadCardsFromDatabase(it)
    }

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
    })

    Surface() {
        Column() {
            val cards = localViewModel.deckCards.value
            LazyColumn {
                items(items = cards) { item ->
                    OneCard(item, localViewModel, commonViewModel)

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OneCard(card: AtomicNote, localViewModel: CardsViewModel, commonViewModel: CommonViewModel){
    ElevatedCard (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    commonViewModel.setManualLeft(card.id)
                    Timber.d("Long Click!")
                }
            )

    ){
        var question by remember { mutableStateOf(card.question ?: "MISSING") }
        var answer by remember { mutableStateOf(card.answer) }
        var buttonVisible by remember { mutableStateOf(false) }

        // todo these remember statements survive recomposition
        // this is a problem if viewing a different deck

        Column {
            Text("[[${card.id}]]", fontSize = 12.sp,  modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = LocalContentColor.current.copy(alpha = 0.5f)
            )
            Text("${card.question?:""} - ${card.answer}", fontSize = 12.sp,  modifier = Modifier
                .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = LocalContentColor.current.copy(alpha = 0.5f)
            )
            Row {
                TextField(
                    value = question,
                    onValueChange = {question = it; buttonVisible = true},
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.5f)
                )
                TextField(
                    value = answer,
                    onValueChange = {answer = it; buttonVisible = true},
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.5f)
                )
            }
            if (buttonVisible) {
                Button(onClick = {
                    buttonVisible = false
                    localViewModel.updateCardValues(card.id, question, answer)
                },
                    modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()) {
                    Text(text = "Update")

                }
            }
        }

    }
}