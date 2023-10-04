package ml.nandor.confusegroups.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManualConfusionsScreen(viewModel: MainViewModel){

    val deckName = viewModel.comparisonDeck2.value!!

    BackHandler(onBack = {
        viewModel.setComparisonDeck2(null)
    })

    Column() {
        Text(
            text = deckName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .wrapContentSize()
            ,
            fontSize = 32.sp
        )

        var last:Int? = null
        LazyColumn {
            items(items = viewModel.allCardsGrouped.value) {it ->
                if (it.second != last){
                    last = it.second
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                ){
                    Text("${it.first} - ${it.second}", fontSize = 24.sp, modifier = Modifier.padding(8.dp))
                }

            }
        }
    }
}
