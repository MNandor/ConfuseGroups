package ml.nandor.confusegroups.presentation.item

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.ui.theme.ConfuseGroupsTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteInAList(it: AtomicNote, callback: (AtomicNote) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = {
                    callback(it)
                }
            )
    ) {
        Row(
            // Make the Row just tall enough to contain the Texts
            // This makes sure that the fillMaxHeight() of the Divider
            // Doesn't fill the entire screen
            // Because the height is limited by Row
            // https://developer.android.com/develop/ui/compose/layouts/intrinsic-measurements
            Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                "${it.question}",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(10.0f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Divider(modifier = Modifier.width(2.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            Text(
                "${it.answer}",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(10.0f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddNoteToList(callback: (Unit) -> Unit = {}){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = {
                    callback(Unit)
                }
            )
            .height(IntrinsicSize.Min)
    ) {
        Text(
            "+",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Dark Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, name = "Light Mode")
@Composable
fun SampleNoteInAList() {
    val note = AtomicNote("1A2B3C", "Woof", "Animals", "Dog", "Dogs bark")
    val longNote = AtomicNote("1A2B3C", "Woof! Bark! Woof-bark!", "Animals", "Dog", "Dogs bark")
    val superLongNote = AtomicNote("1A2B3C", "Woof! Bark! Woof-bark!", "Animals", "A dog is a canine animal that likes to play fetch.", "Dogs bark")

    // Compose Docks recommend having the Theme-Surface pair in Previews
    // See also Theme.kt file
    ConfuseGroupsTheme {
        Surface {
            Column {
                NoteInAList(it = note, {})
                NoteInAList(it = longNote, {})
                NoteInAList(it = superLongNote, {})
                AddNoteToList()
            }
        }
    }

}