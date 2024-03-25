package ml.nandor.confusegroups.presentation.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
                    Text("${item.question} - ${item.answer}")

                }
            }
        }
    }
}