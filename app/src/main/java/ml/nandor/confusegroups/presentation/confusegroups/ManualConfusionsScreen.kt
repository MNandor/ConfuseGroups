package ml.nandor.confusegroups.presentation.confusegroups

import android.widget.EditText
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.presentation.MainViewModel
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import ml.nandor.confusegroups.presentation.item.NoteInAList
import timber.log.Timber

@Composable
fun ManualConfusionsScreen(commonViewModel:CommonViewModel){

    val deckName = commonViewModel.selectedDeck.value!!

    val localViewModel:ConfuseGroupsViewModel = hiltViewModel()

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
                    DrawnConfuseGroup(group = it, viewModel = localViewModel)


            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DrawnConfuseGroup(group: ConfuseGroupToAddTo, viewModel: ConfuseGroupsViewModel){

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
                Text(newName.value, modifier = Modifier
                    .combinedClickable {
                        editing.value = true
                    }
                    .fillMaxWidth()
                    .padding(8.dp),
                    textAlign = TextAlign.Center
                )

            } else{
                TextField(value = newName.value, onValueChange = {
                    newName.value = it
                },
                    modifier = Modifier.fillMaxWidth())
                Button(onClick = { editing.value = false; viewModel.renameConfuseGroup(groupID = group.confuseGroup.id, newName = newName.value)},
                    modifier = Modifier.fillMaxWidth()) {
                    Text("Update")

                }
            }
            LazyColumn(
                modifier = Modifier
                    .heightIn(0.dp, 200.dp) //constrain height
                    .fillMaxWidth()
            ){
                items(items = group.associatedNotes){item ->
                    NoteInAList(item, callback = {
                    })
                }
            }

        }


    }

}


