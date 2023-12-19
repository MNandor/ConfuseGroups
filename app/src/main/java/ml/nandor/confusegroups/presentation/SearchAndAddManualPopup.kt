package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ml.nandor.confusegroups.presentation.item.NoteInAList
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchAndAddManualPopup(viewModel: MainViewModel) {

    val cardLeft = viewModel.manualCardLeft.value
    val visible = cardLeft != null

    var searchString = viewModel.manualRightSearchTerm.value
    val searchResults = viewModel.allCardsForManualFiltered.value

    if (visible) {
        Dialog(onDismissRequest = { viewModel.setManualLeft(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Column() {
                    Text(
                        cardLeft!!,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                    TextField(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                        value = searchString,
                        onValueChange = { it: String -> viewModel.setManualRightSearchTerm(it) },
                        label = { Text("Search for other cards") }
                    )

                    TextButton(
                        onClick = {
                            viewModel.setManualLeft(null)
                        },
                    ) {
                        Text("Close")
                    }
                    LazyColumn {
                        items(items = searchResults) { item ->
                            ElevatedCard(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                NoteInAList(item, callback = {
                                    Timber.d("$cardLeft - ${item.id}")
                                    viewModel.addManualConfusion(cardLeft, item.id)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

}