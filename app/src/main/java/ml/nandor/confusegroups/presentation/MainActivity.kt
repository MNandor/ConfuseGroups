package ml.nandor.confusegroups.presentation

import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ml.nandor.confusegroups.R
import ml.nandor.confusegroups.ui.theme.ConfuseGroupsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val soundPool: SoundPool = SoundPool.Builder().build()
        val goodSoundID = soundPool.load(this, R.raw.success, 1)
        val badSoundID = soundPool.load(this, R.raw.fail, 1)

        val playNoise = {success:Boolean ->
            if (success){
                soundPool.play(goodSoundID, 1.0f, 1.0f, 0, 0, 1.0f)
            } else {
                soundPool.play(badSoundID, 1.0f, 1.0f, 0, 0, 1.0f)
            }
        }

        setContent {
            ConfuseGroupsTheme {
                val viewModel: MainViewModel = hiltViewModel()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (viewModel.selectedDeck.value != null){
                        ReviewScreen(viewModel, playNoise)
                    } else {
                        if (viewModel.comparisonDeck2.value != null) {
                            ManualConfusionsScreen(viewModel = viewModel)
                        } else if (viewModel.comparisonDeck.value != null){
                            ComparisonScreen(viewModel)
                        } else {
                            DecksScreen(viewModel)
                        }
                    }

                    val popups = viewModel.comparisonPopups.value
                    Box {
                        for (popup in popups){
                            ComparisonPopup(text = popup, viewModel)
                        }
                    }

                    SearchAndAddManualPopup(viewModel = viewModel)

                }
            }
        }
    }






}