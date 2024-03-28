package ml.nandor.confusegroups.presentation.xport

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ml.nandor.confusegroups.presentation.common.CommonViewModel

@Composable
fun XportScreen(commonViewModel: CommonViewModel){
    Text("Hi")

    BackHandler(onBack = {
        commonViewModel.deselectDeck()
    })

}