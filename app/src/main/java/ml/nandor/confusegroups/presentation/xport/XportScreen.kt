package ml.nandor.confusegroups.presentation.xport

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        Button(onClick = { localViewModel.loadExportString(commonViewModel.selectedDeck.value) }) {
            Text("Export")

        }
        TextField(value = localViewModel.exportString.value, onValueChange = {})
    }

}