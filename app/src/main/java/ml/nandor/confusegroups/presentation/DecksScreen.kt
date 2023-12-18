package ml.nandor.confusegroups.presentation

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.model.AtomicNote

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
        RenameDeckPopup(viewModel)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeckItem(text: String, viewModel: MainViewModel) {
    val deckSize = viewModel.getDeckSize(text)
    val context = LocalContext.current

    val deckDisplayName = viewModel.decks.value.find { it.name == text }?.displayName ?: "[[$text]]"

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(164.dp)
            .combinedClickable(
                onClick = {
                    if (deckSize > 3) {
                        viewModel.selectDeck(text)
                    }
                    else {
                        Toast.makeText(context, "Minimum deck size is 4", Toast.LENGTH_SHORT).show()
                    }
                }
            )
    ) {
        Column(){
            Row(modifier = Modifier
                .fillMaxWidth()
            ){

                Text(
                    modifier = Modifier
                        .weight(0.15f).fillMaxWidth()
                        .padding(top = 24.dp)
                        .wrapContentSize()
                        .height(64.dp)
                        .align(Alignment.CenterVertically),

                    text = viewModel.getDeckSize(text).toString(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = deckDisplayName,
                    modifier = Modifier
                        .weight(0.7f).fillMaxWidth()
                        .padding(top = 16.dp)
                        .wrapContentSize()
                        .height(64.dp)
                    ,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
                Text(
                    modifier = Modifier
                        .weight(0.15f).fillMaxWidth()
                        .padding(top = 24.dp)
                        //.wrapContentSize()
                        .height(64.dp)
                        .align(Alignment.CenterVertically)
                    ,
                    text = deckSize.toString(),
                    color = if (deckSize > 3) Color.Unspecified else Color.Red,
                    textAlign = TextAlign.Center
                )
             }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.ADDING) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add card to deck")
                }
                IconButton(onClick = { viewModel.setComparisonDeck(text) }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Show deck mistakes")
                }
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.RENAME) }) {
                    Icon(Icons.Filled.Person, contentDescription = "Rename deck")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { viewModel.enterDeckActionMode(text, MainViewModel.DeckAction.INSPECTION) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit deck data")
                }
//                IconButton(onClick = { viewModel.setComparisonDeck2(text) }) {
//                    Icon(Icons.Filled.DateRange, contentDescription = "Show deck mistakes")
//                }
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
                                val card = AtomicNote(id = Util.getCardName(), answer = answer, deck = deckName, question = question)
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
                            Text(item.id+" - "+item.answer)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDeckPopup(viewModel: MainViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val deckDisplayName = viewModel.decks.value.find { it.name == deckName }?.displayName ?: "UNNAMED"
    val visible = viewModel.deckActionBeingTaken.value == MainViewModel.DeckAction.RENAME

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
                    TextField(
                        value = inputText,
                        onValueChange = {inputText = it},
                        label = { Text(deckDisplayName) },
                        maxLines = 1,
                        modifier = Modifier.padding(16.dp)
                    )

                    TextButton(
                        onClick = {
                            viewModel.renameDeck(deckName, inputText)
                            viewModel.enterDeckActionMode()
                        },
                    ) {
                        Text("Rename Deck")
                    }
                }

            }
        }
    }
}