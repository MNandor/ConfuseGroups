package ml.nandor.confusegroups.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComparisonPopup(text: String, viewModel: MainViewModel) {
    var offset by remember { mutableStateOf(Offset(0.0f, 0.0f)) }


    Box( modifier = Modifier
        .offset{IntOffset(offset.x.toInt(), offset.y.toInt())}
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
            change.consume()
                offset = Offset(offset.x + dragAmount.x, offset.y+dragAmount.y)
            }
        }
        .fillMaxWidth(0.3f)
        .aspectRatio(1.0f)
        .combinedClickable (
            onClick = {},
            onLongClick = {
                viewModel.removeComparisonPopup(text)
            }
        )
    ){
        Card(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Text(text)
        }
    }

}