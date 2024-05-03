package ml.nandor.confusegroups.presentation.confusegroups

import android.graphics.drawable.Icon
import android.widget.EditText
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.presentation.MainViewModel
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import ml.nandor.confusegroups.presentation.decks.DecksViewModel
import ml.nandor.confusegroups.presentation.item.NoteInAList
import timber.log.Timber

@Composable
fun ManualConfusionsScreen(commonViewModel:CommonViewModel){

    val deckName = commonViewModel.selectedDeck.value!!

    val localViewModel:ConfuseGroupsViewModel = hiltViewModel()

    val interfaceImp = object: CanManageAConfuseGroup{
        override fun renameConfuseGroup(groupID: String, newName: String) {
            localViewModel.renameConfuseGroup(groupID, newName)
        }

        override fun deleteFromConfuseGroup(groupID: String, noteID: String) {
            localViewModel.selectCardForRemovalFromGroup(groupID, noteID)
        }

    }

    commonViewModel.selectedDeck.value?.let {
        Timber.d("Loading from $it")
        localViewModel.loadGroupsFromDatabse(it)
    }

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
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
            items(items = localViewModel.deckGroups.value) {it ->
                if (it.confuseGroup != null && localViewModel.readyToCompose.value)
                    DrawnConfuseGroup(group = it, interfaceImp, )


            }
        }
    }

    RemoveFromGroupPopup(localViewModel)
}

// Extracted to an Interface
// This is so @Preview can implement an empty Interface
// Otherwise literally just a wrapper for ViewModel functions
interface CanManageAConfuseGroup{
    fun renameConfuseGroup(groupID: String, newName: String)

    fun deleteFromConfuseGroup(groupID: String, noteID: String)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DrawnConfuseGroup(group: ConfuseGroupToAddTo, managerInterface: CanManageAConfuseGroup){

    Timber.d("Composing ${group.confuseGroup?.id}")

    if (group.confuseGroup == null) return

    val newName = remember{ mutableStateOf(group.confuseGroup.displayName?:"MISSING") }
    val editing = remember{ mutableStateOf(false) }

    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        Column {
            if (!editing.value){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(newName.value, modifier = Modifier
                        .combinedClickable {
                            editing.value = true
                        }
                        .weight(1.0f)
                        .padding(8.dp),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (editing.value){
                        IconButton(onClick = { editing.value = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Rename Group")
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Group")
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Share, contentDescription = "Combine with another Group")
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add to Group")
                        }
                    } else {
                        IconButton(onClick = { editing.value = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Enter Edit Mode")
                        }
                    }
                }

            } else{
                TextField(value = newName.value, onValueChange = {
                    newName.value = it
                },
                    modifier = Modifier.fillMaxWidth())
                Button(onClick = { editing.value = false; managerInterface.renameConfuseGroup(group.confuseGroup.id, newName.value)},
                    modifier = Modifier.fillMaxWidth()) {
                    Text("Update")

                }
            }
            LazyColumn(
                modifier = Modifier
                    .heightIn(0.dp, 200.dp) //constrain height
                    .fillMaxWidth()
            ) {
                items(items = group.associatedNotes) { item ->
                    Box(contentAlignment = Alignment.Center) {
                        NoteInAList(item, callback = {
                        })
                        if (editing.value) {
                            IconButton(onClick = {
                                managerInterface.deleteFromConfuseGroup(
                                    group.confuseGroup.id,
                                    item.id
                                )
                            }, modifier = Modifier) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete From Group")
                            }
                        }
                    }
                }
            }

        }


    }

}


@Preview
@Composable
fun PreviewDrawnConfuseGroup(){
    val group = ConfuseGroupToAddTo(
        confuseGroup = ConfuseGroup("1a2b3c", "Animals"),
        associatedNotes = listOf(
            AtomicNote("111", "Woof", "Animdeck", "Dog", "Dogs mnemonic"),
            AtomicNote("222", "Meow", "Animdeck", "Cat", "Cats mnemonic"),
            AtomicNote("333", "Chirp", "Animdeck", "Bird", "Birds mnemonic"),
        )
    )

    val emptyInterface = object : CanManageAConfuseGroup{
        override fun renameConfuseGroup(groupID: String, newName: String) {

        }

        override fun deleteFromConfuseGroup(groupID: String, noteID: String) {
            TODO("Not yet implemented")
        }

    }

    DrawnConfuseGroup(group = group, emptyInterface)


}

@Composable
fun RemoveFromGroupPopup(viewModel: ConfuseGroupsViewModel) {
    val visible = viewModel.isADeletionPending.value

    if (visible) {
        AlertDialog(
            onDismissRequest = { viewModel.decideOnRemovalFromGroup(false) },
            title = {
                Text(text = "Really remove from group?")
            },
            text = { Text("The card won't be deleted, but will no longer be part of this group.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.decideOnRemovalFromGroup(true)
                    },
                ) {
                    Text("Yes, remove")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.decideOnRemovalFromGroup(false) },
                ) {
                    Text("No, don't remove")
                }
            }
        )
    }
}

