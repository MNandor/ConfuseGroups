package ml.nandor.confusegroups.presentation.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    commonViewModel.comparisonDeck3.value?.let { localViewModel.loadCardsFromDatabase(it) }



    Surface() {
        Column() {
            val cards = localViewModel.deckCards.value
            LazyColumn {
                items(items = cards) { item ->
                    OneCard(item)

                }
            }
        }
    }
}

@Composable
fun OneCard(card: AtomicNote){
    ElevatedCard (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()

    ){

        Column {
            Text("[[${card.id}]]", fontSize = 12.sp,  modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = LocalContentColor.current.copy(alpha = 0.5f)
            )
            Row {
                Text(card.question ?: "[[MISSING]]", fontSize = 16.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.5f),
                )
                Text(card.answer, fontSize = 16.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.5f)
                )
            }
        }

    }
}