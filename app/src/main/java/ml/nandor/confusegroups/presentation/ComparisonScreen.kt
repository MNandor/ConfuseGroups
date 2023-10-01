package ml.nandor.confusegroups.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComparisonScreen(viewModel: MainViewModel){

    val deckName = viewModel.comparisonDeck.value!!

    BackHandler(onBack = {
        viewModel.setComparisonDeck(null)
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

        if (viewModel.correlations.value.isEmpty()){
            Text(
                text = "Loading Correlations. This might take longer for larger decks.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentSize()
                ,
                fontSize = 16.sp
            )
        }
        
        LazyColumn {
            items(items = viewModel.correlations.value) {it ->
                OutlinedCard (
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.0f)
                        .padding(16.dp)
                ){

                    Text(
                        text = (it.correlation*100).toInt().toString()+"%",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .wrapContentSize()
                        ,
                        fontSize = 16.sp
                    )

                    Row(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        SimpleSmallCard(it.leftCard.question)
                        SimpleSmallCard(it.rightCard.question)
                    }

                    Row(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        SimpleSmallCard(it.leftCard.answer)
                        SimpleSmallCard(it.rightCard.answer)
                    }
                }


            }
        }
    }
}

@Composable
fun RowScope.SimpleSmallCard(text:String, ){
    ElevatedCard(
        modifier = Modifier
            .weight(1.0f)
            .padding(16.dp)
            .fillMaxWidth()
            .aspectRatio(1.0f)


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