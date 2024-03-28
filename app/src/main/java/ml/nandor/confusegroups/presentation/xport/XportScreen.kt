package ml.nandor.confusegroups.presentation.xport

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ml.nandor.confusegroups.presentation.common.CommonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XportScreen(commonViewModel: CommonViewModel){

    val localViewModel:XportViewModel = hiltViewModel()

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
    })

    Column {
        Button(onClick = { localViewModel.loadExportString(commonViewModel.selectedDeck.value) }, modifier = Modifier.fillMaxWidth()) {
            Text("Export")

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