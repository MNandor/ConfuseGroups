package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DecksScreen(viewModel: MainViewModel){
    Column() {
        DeckItem("Main Deck", viewModel)
        DeckItem("+", viewModel)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeckItem(text: String, viewModel: MainViewModel) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(128.dp)
            .combinedClickable (
                onClick = {
                    viewModel.selectDeck(text)
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
}