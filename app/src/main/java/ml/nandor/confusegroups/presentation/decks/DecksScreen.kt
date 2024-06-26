package ml.nandor.confusegroups.presentation.decks

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ml.nandor.confusegroups.R
import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import timber.log.Timber

@Composable
fun DecksScreen(commonViewModel: CommonViewModel) {
    Timber.d("Launched")
    val localViewModel: DecksViewModel = hiltViewModel()
    localViewModel.listDecksFromDatabase()

    Surface() {
        Column() {
            val decks = localViewModel.decks.value
            val ddecks: MutableList<String?> = decks.map { it.name }.toMutableList()
            ddecks.add(null)
            LazyColumn {
                items(items = ddecks) { item ->
                    if (item == null) {
                        AddDeck(localViewModel)
                    } else {
                        DeckItem(item, localViewModel, commonViewModel)
                    }

                }
            }
        }

        InitialTutorialPopup(localViewModel)
        DeleteDeckPopup(localViewModel)
        EditDeckSettingsPopup(localViewModel)
        AddToDeckPopup(localViewModel)
        InspectDeckPopup(localViewModel)
        RenameDeckPopup(localViewModel)
        ReverseDeckPopup(localViewModel)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeckItem(text: String, viewModel: DecksViewModel, commonViewModel: CommonViewModel) {
    val deckSize = viewModel.getDeckSizeFromDeckName(text)
    val deckLevel = viewModel.getDeckLevelFromDeckName(text)
    val context = LocalContext.current

    val theDeck = viewModel.decks.value.find { it.name == text }

    val deckDisplayName = theDeck?.displayName ?: "[[$text]]"
    val displayNameOpacity =
        theDeck?.displayName?.let { 1.0f } ?: 0.5f // funniest null check i've ever written

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(164.dp)
            .combinedClickable(
                onClick = {
                    if (deckSize > 3) {
                        commonViewModel.selectDeck(text, CommonViewModel.DeckOpenMode.REVIEW)
                    } else {
                        Toast
                            .makeText(context, "Minimum deck size is 4", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .wrapContentSize()
                        .height(64.dp)
                        .align(Alignment.CenterVertically),

                    text = deckLevel.toString(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = deckDisplayName,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .wrapContentSize()
                        .height(64.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    color = LocalContentColor.current.copy(alpha = displayNameOpacity)
                )
                Text(
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        //.wrapContentSize()
                        .height(64.dp)
                        .align(Alignment.CenterVertically),
                    text = deckSize.toString(),
                    color = if (deckSize > 3) Color.Unspecified else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.ADDING
                    )
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add one card to deck")
                }
                IconButton(onClick = {
                    commonViewModel.selectDeck(
                        text,
                        CommonViewModel.DeckOpenMode.CORRELATIONS
                    )
                }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Show correlations")
                }
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.RENAME
                    )
                }) {
                    Icon(Icons.Filled.Person, contentDescription = "Rename deck")
                }
                IconButton(onClick = {
                    commonViewModel.selectDeck(text, CommonViewModel.DeckOpenMode.VIEWCARDS)
                }) {
                    Icon(Icons.Filled.Menu, contentDescription = "View Cards")
                }
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.REVERSE
                    )
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Reverse Deck")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.INSPECTION
                    )
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit deck data")
                }
                IconButton(onClick = {
                    commonViewModel.selectDeck(
                        text,
                        CommonViewModel.DeckOpenMode.CONFUSEGROUPS
                    )
                }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Show confusegroups")
                }
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.EDITING
                    )
                }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Edit deck settings")
                }
                IconButton(onClick = {
                    viewModel.enterDeckActionMode(
                        text,
                        DecksViewModel.DeckAction.DELETION
                    )
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete deck")
                }
                IconButton(onClick = {
                    commonViewModel.selectDeck(
                        text,
                        CommonViewModel.DeckOpenMode.XPORT
                    )
                }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Import/export mode")
                }
            }
        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddDeck(viewModel: DecksViewModel) {
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
        Column() {
            Text(
                text = "+",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentSize()
                    .height(64.dp),
                textAlign = TextAlign.Center,
                fontSize = 48.sp
            )
        }

    }
}

@Composable
fun DeleteDeckPopup(viewModel: DecksViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.DELETION

    if (visible) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckSettingsPopup(viewModel: DecksViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.EDITING
    if (visible) {
        val corPref = remember { mutableStateOf("") }
        val grpPref = remember { mutableStateOf("") }
        val ranPref = remember { mutableStateOf("") }
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            ) {
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        deckName!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )

                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("New cards per level: ")
                        Text(viewModel.editedDeckState.value?.newCardsPerLevel.toString())
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("Success multiplier: ")
                        Text(viewModel.editedDeckState.value?.successMultiplier.toString())
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("Confuse exponent: ")
                        Text(viewModel.editedDeckState.value?.confuseExponent.toString())
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("Correlation preference")
                        Text(viewModel.thisDeck.value?.correlationPreference.toString())
                        TextField(value = corPref.value, onValueChange = { corPref.value = it })
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("Group preference")
                        Text(viewModel.thisDeck.value?.confgroupPreference.toString())
                        TextField(value = grpPref.value, onValueChange = { grpPref.value = it })
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text("Random preference")
                        Text(viewModel.thisDeck.value?.randomPreference.toString())
                        TextField(value = ranPref.value, onValueChange = { ranPref.value = it })
                    }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Button(onClick = {
                            viewModel.setPreferences(corPref.value, grpPref.value, ranPref.value)
                        }) {
                            Text("Update!")
                        }
                    }
                    Text("Explanation")
                    Text(
                        fontSize = 8.sp,
                        lineHeight = 8.sp,
                        modifier = Modifier.padding(8.dp),
                        text = """
                        New Cards Per Level
                        Limiting this means you can start practicing already learned cards before seeing ALL cards at least once.
                        If set to -1, the limit is auto-calculated based on deck size.
                        
                        Success Multiplier
                        Determines how fast gaps between successful reviews grow.
                        This actually barely affects the order of Review cards in relation to each other, but a higher value causes New cards to show sooner.
                        (Subject to change)
                        
                        Confuse Exponent
                        (Legacy, subject to removal)
                        
                        Correlation Preference
                        Picking the wrong answer increases the "correlation" between the card questioned and the card that contained the wrong answer.
                        This determines how much more likely that same wrong answer is offered the next time the card is reviewed.
                        
                        Group Preference
                        How much more likely are cards with at least one ConfuseGroup in common are to be offered as pickable options.
                        
                        Random Preference
                        Randomly pick pickable wrong answers as opposed to the above two methods.
                    """.trimIndent()
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToDeckPopup(viewModel: DecksViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.ADDING

    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())

                ) {
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
                    ) {
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
                                val card = AtomicNote(
                                    id = Util.getCardName(),
                                    answer = answer,
                                    deck = deckName,
                                    question = question
                                )
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
fun InspectDeckPopup(viewModel: DecksViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.INSPECTION
    val cards = viewModel.inspectedDeckCards.value

    var inputText by remember { mutableStateOf("") }


    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        deckName!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxHeight(0.5f)
                            .heightIn(0.dp, 200.dp)
                    ) {
                        items(items = cards) { item ->
                            Text(item.question + " - " + item.answer)

                        }
                    }
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Que1-Ans1;Que1-Ans2") },
                        maxLines = 1,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
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
                                viewModel.addCardsAfterDeckInspection(inputText)
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
fun RenameDeckPopup(viewModel: DecksViewModel) {
    val deckName = viewModel.deckBeingAccessed.value
    val deckDisplayName =
        viewModel.decks.value.find { it.name == deckName }?.displayName ?: "UNNAMED"
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.RENAME

    var inputText by remember { mutableStateOf("") }


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
                        onValueChange = { inputText = it },
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

@Composable
fun ReverseDeckPopup(viewModel: DecksViewModel) {
    val visible = viewModel.deckActionBeingTaken.value == DecksViewModel.DeckAction.REVERSE

    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                TextButton(
                    onClick = {
                        viewModel.reverseDeck()
                        viewModel.enterDeckActionMode()
                    },
                ) {
                    Text("Reverse Deck!")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitialTutorialPopup(viewModel: DecksViewModel) {
    val visible = viewModel.shouldShowInitialPopup.value

    if (visible) {
        Dialog(onDismissRequest = { viewModel.enterDeckActionMode() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                val pagerState = rememberPagerState(initialPage = 0)
                val scope = rememberCoroutineScope()
                val pageCount = 7
                HorizontalPager(
                    pageCount = pageCount,
                    state = pagerState,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(.9f)
                ) { page ->

                    Column(modifier = Modifier.fillMaxHeight()) {
                        when (page) {
                            0 -> {

                                Text(
                                    "Welcome to ConfuseGroups!",
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )


                                val vec1 =
                                    painterResource(id = R.drawable.logo_confusegroups_importable)

                                Image(
                                    vec1,
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text("The memory game that adapts to your knowledge to keep you challenged!")


                            }

                            1 -> {

                                Text(buildAnnotatedString {
                                    append("A ")
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append("Flashcard")
                                    }
                                    append(" consists of a Front (Question) and Back (Answer) side.")

                                }

                                )

                                Image(
                                    painter = painterResource(id = R.drawable.intro_hito),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )


                                Text(
                                    """                  
                                  You will be shown a Question, and 4 possible Answers.
                                  Pick the right one.
                                  """.trimIndent()
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.intro_hito2),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )

                            }

                            2 -> {

                                Text(
                                    buildAnnotatedString {
                                        append("A collection of Flashcards is called a ")
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        ) {
                                            append("Deck")
                                        }
                                        append(".")
                                        append(" A basic Deck has been created for you to try, but you can always delete it and make your own.")
                                        append(" You can also import Decks from other users.")

                                    }
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.intro_deck),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )

                            }

                            3 -> {

                                Text(
                                    buildAnnotatedString {
                                        append("Two Cards are considered ")
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        ) {
                                            append("Correlated")
                                        }
                                        append(" if you choose one Card's Answer when presented with the other Card's Question.")
                                    }
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.intro_correlation),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )
                                Text("The more often you mistake them for each other, the stronger the Correlation becomes.")

                            }

                            4 -> {

                                Text(
                                    buildAnnotatedString {
                                        append("A ")
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        ) {
                                            append("ConfuseGroup")
                                        }
                                        append(" is formed by two or more Cards that you've identified as interconnected.")
                                    }
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.intro_groups),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )

                                Text("They share something in common and are likely to be Correlated with each other.")

                            }

                            5 -> {

                                Text("A Correlation is between exactly two cards and it's determined by your answers.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("A ConfuseGroup can include any number of cards and is created manually.")
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("You can configure the each Deck to be more likely to show selectable Answers from Correlated cards, or cards that share a ConfuseGroup. This makes the challenge harder than completely random options.")

                                Image(
                                    painter = painterResource(id = R.drawable.intro_settings),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )

                            }

                            6 -> {

                                Text(
                                    """
                                The app adjusts to your learning habits. If you consistently answer a card correctly, it will appear less frequently because you already know it well—no need for extra practice!
                            """.trimIndent()
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.intro_srs),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )
                                Text("If you're familiar with Spaced Repetition Systems, it's the same concept, minus the waiting time.")

                                Image(
                                    painter = painterResource(id = R.drawable.intro_potato),
                                    contentDescription = "Hi",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth(),
                                    colorFilter = ColorFilter.tint(
                                        LocalContentColor.current
                                    )
                                )
                            }

                        }
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            viewModel.hideInitialPopup()
                        },
                    ) {
                        if (pagerState.currentPage != pageCount -1)
                            Text("Skip")
                        
                    }

                    TextButton(
                        onClick = {
                            if (pagerState.currentPage != pageCount -1)
                                scope.launch {
                                    pagerState.animateScrollToPage((pagerState.currentPage + 1) % pageCount)
                                }
                            else
                                viewModel.hideInitialPopup()
                        },
                    ) {
                        if (pagerState.currentPage == pageCount -1)
                            Text("Close")
                        else
                            Text("Next")
                    }

                }
            }
        }
    }
}