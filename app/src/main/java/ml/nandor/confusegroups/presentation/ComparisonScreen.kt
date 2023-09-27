package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComparisonScreen(viewModel: MainViewModel){

    val deckName = viewModel.comparisonDeck.value!!

    Column() {
        Text(
            text = deckName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .wrapContentSize()
                .height(64.dp)
            ,
            fontSize = 32.sp
        )
        
        LazyColumn {
            items(items = viewModel.correlations.value) {it ->
                Text(it.toString())

            }
        }
    }
}