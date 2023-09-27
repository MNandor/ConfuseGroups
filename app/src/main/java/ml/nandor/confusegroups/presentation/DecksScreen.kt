package ml.nandor.confusegroups.presentation

import android.app.Dialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ml.nandor.confusegroups.domain.model.AtomicNote
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
        
        DeleteDeckPopup(viewModel)
        EditDeckSettingsPopup(viewModel)
        AddToDeckPopup(viewModel)
        InspectDeckPopup(viewModel)
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
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.ADDING) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add card to deck")
                }
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.INSPECTION) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit deck data")
                }
                IconButton(onClick = { viewModel.setComparisonDeck(text) }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Show deck mistakes")
                }
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.EDITING) }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Edit deck settings")
                }
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.DELETION) }) {
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
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == MainViewModel.DeckAction.DELETION

    if (visible){
        AlertDialog(
            onDismissRequest = { viewModel.enterDeckActionMode() },
            title = {
                Text(text = "Delete deck ${deckName}?")
            },
            text = { Text("The deck, along with all of its cards will be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDeck()
                    },
                ) {
                    Text("Yes, delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.enterDeckActionMode() },
                ) {
                    Text("No, don't delete")
                }
            }
        )
    }
}

@Composable
fun EditDeckSettingsPopup(viewModel: MainViewModel){
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == MainViewModel.DeckAction.EDITING
    if (visible){
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ){
                Column() {
                    Text(deckName!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ){
                        Text("New cards per level: ")
                        Text(viewModel.editedDeckState.value?.newCardsPerLevel.toString())
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ){
                        Text("Success multiplier: ")
                        Text(viewModel.editedDeckState.value?.successMultiplier.toString())
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ){
                        Text("Confuse exponent: ")
                        Text(viewModel.editedDeckState.value?.confuseExponent.toString())
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToDeckPopup(viewModel: MainViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == MainViewModel.DeckAction.ADDING

    var question by remember{ mutableStateOf("") }
    var answer by remember{ mutableStateOf("") }

    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Column() {
                    Text(
                        deckName!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )

                    TextField(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                        value = question,
                        onValueChange = { question = it },
                        label = { Text("Question (Front):") }
                    )
                    TextField(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Answer (Back):") }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        TextButton(
                            onClick = {
                                question = ""
                                answer = ""
                                viewModel.enterDeckActionMode()
                            },
                        ) {
                            Text("Close")
                        }

                        TextButton(
                            onClick = {
                                val card = AtomicNote(question = question, answer = answer, deck = deckName)
                                viewModel.addCard(card)
                                question = ""
                                answer = ""
                            },
                        ) {
                        Text("Add")
                    }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectDeckPopup(viewModel: MainViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == MainViewModel.DeckAction.INSPECTION
    val cards = viewModel.inspectedDeckCards.value

    var inputText by remember{ mutableStateOf("") }


    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Column() {
                    Text(
                        deckName!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                    LazyColumn(
                        modifier = Modifier.padding(8.dp).fillMaxHeight(0.5f)
                    ) {
                        items(items = cards) { item ->
                            Text(item.question+" - "+item.answer)

                        }
                    }
                    TextField(
                        value = inputText,
                        onValueChange = {inputText = it},
                        label = { Text("Que1-Ans1;Que1-Ans2") },
                        maxLines = 1,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        TextButton(
                            onClick = {
                                inputText = ""
                                viewModel.enterDeckActionMode()
                            },
                        ) {
                            Text("Close")
                        }

                        TextButton(
                            onClick = {
                                viewModel.loadDeckFromText(inputText)
                                inputText = ""
                              viewModel.enterDeckActionMode()
                            },
                        ) {
                            Text("Add")
                        }
                    }

                }

            }
        }
    }
}