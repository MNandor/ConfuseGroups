package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ml.nandor.confusegroups.domain.model.Deck

@Composable
fun DecksScreen(viewModel: MainViewModel){
    Surface() {
        Column() {
            val decks = viewModel.decks.value
            val ddecks: MutableList<String?> = decks.map { it.name }.toMutableList()
            ddecks.add(null)
            LazyColumn {
                items(items = ddecks) { item ->
                    if (item == null) {
                        AddDeck(viewModel)
                    } else {
                        DeckItem(item, viewModel)
                    }

                }
            }
        }
        
        DeleteDeckPopup(viewModel = viewModel)
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
            .combinedClickable(
                onClick = {
                    viewModel.selectDeck(text)
                }
            )
    ) {
        Column(){
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .wrapContentSize()
                    .height(64.dp)
                ,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit deck data")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Edit deck settings")
                }
                IconButton(onClick = { viewModel.enterDeleteMode(text) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete deck")
                }
            }
        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddDeck(viewModel: MainViewModel) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(128.dp)
            .combinedClickable(
                onClick = {
                    viewModel.createDeck()
                }
            )
    ) {
        Column(){
            Text(
                text = "+",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentSize()
                    .height(64.dp)
                ,
                textAlign = TextAlign.Center,
                fontSize = 48.sp
            )
        }

    }
}

@Composable
fun DeleteDeckPopup(viewModel: MainViewModel){
    val deckName = viewModel.deckBeingDeleted.value
    val visible = deckName != null

    if (visible){
        AlertDialog(
            onDismissRequest = { viewModel.enterDeleteMode(null) },
            title = {
                Text(text = "Delete deck ${deckName}?")
            },
            text = { Text("The deck, along with all of its cards will be deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeck()
                    },
                ) {
                    Text("Yes, delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.enterDeleteMode(null) },
                ) {
                    Text("No, don't delete")
                }
            }
        )
    }
}

