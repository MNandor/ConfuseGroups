package ml.nandor.confusegroups.presentation.xport

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.presentation.common.CommonViewModel
import ml.nandor.exportDatabaseFile.ExportDatabaseFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XportScreen(commonViewModel: CommonViewModel){

    val localViewModel:XportViewModel = hiltViewModel()
    val context = LocalContext.current

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
    })

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
            Button(onClick = { localViewModel.loadExportString(commonViewModel.selectedDeck.value) }) {
                Text("Export to Text")

            }
            Button(onClick = { ExportDatabaseFile.exportDBToStorage(context) }) {
                Text("Export Entire DB")

            }

        }
        TextField(value = localViewModel.exportString.value, onValueChange = {}, modifier = Modifier
            .fillMaxHeight(.40f)
            .fillMaxWidth()
        )
        
        TextField(value = localViewModel.importableText.value, onValueChange = {localViewModel.setCompareString(it)}, modifier = Modifier
            .fillMaxHeight(.5f)
            .fillMaxWidth()
        )

        TextField(value = localViewModel.diffed.value, onValueChange = {}, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.8f)
        )
        Button(onClick = { localViewModel.importFromText() }, modifier = Modifier.fillMaxWidth()) {
            Text("Import")

        }

    }

}