package ml.nandor.confusegroups.presentation.item

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ml.nandor.confusegroups.domain.model.AtomicNote

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteInAList(it: AtomicNote, callback: (AtomicNote) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable (
                onClick = {
                    callback(it)
                }
            )
    ) {
        Text("${it.question} - ${it.answer}", fontSize = 24.sp, modifier = Modifier.padding(8.dp))
    }
}