package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import ml.nandor.confusegroups.presentation.item.NoteInAList
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchAndAddManualPopup(commonViewModel: CommonViewModel) {

    val cardLeft = commonViewModel.manualCardLeft.value
    val visible = cardLeft != null

    var searchString = commonViewModel.manualRightSearchTerm.value

    val newSearchResults = commonViewModel.groupToAddTo.value

    if (visible) {
        Dialog(onDismissRequest = { commonViewModel.setManualLeft(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
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
                        onValueChange = { it: String -> commonViewModel.setManualRightSearchTerm(it) },
                        label = { Text("Search for other cards") }
                    )

                    TextButton(
                        onClick = {
                            commonViewModel.setManualLeft(null)
                        },
                    ) {
                        Text("Close")
                    }


                    LazyColumn {
                        items(items = newSearchResults) { item ->
                            ElevatedCard(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column {

                                    Text(item.confuseGroup?.displayName?:"UNGROUPED",
                                        modifier = Modifier.combinedClickable {
                                            Timber.d("Clicked - ${item.confuseGroup?.displayName?:"UNGROUPED"}")
                                            val groupID = item.confuseGroup?.id
                                            if (groupID != null)
                                                commonViewModel.joinExistingGroup(groupID)
                                        })
                                    // we're nesting lazycolumns
                                    // that's a bad idea
                                    // to make it work, we need to define the height of the inner lazycolumn
                                    InnerColumn(cards = item.associatedNotes, commonViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun InnerColumn(cards: List<AtomicNote>, commonViewModel: CommonViewModel){
    LazyColumn(
        modifier = Modifier
            .heightIn(0.dp, 200.dp) //constrain height
            .fillMaxWidth()
    ){
        items(items = cards){item ->
//            Text("${item.question} - ${item.answer}")
            NoteInAList(item, callback = {
                Timber.d("Clicked - ${item.question}")
//                viewModel.addManualConfusion(cardLeft, item.id)
                commonViewModel.createGroupWithOtherCard(item.id)
                // todo instead ask if user wants to merge into the existing group
            })
        }
    }
}