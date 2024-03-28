package ml.nandor.confusegroups.presentation.common

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
import ml.nandor.confusegroups.presentation.ComparisonPopup
import ml.nandor.confusegroups.presentation.ComparisonScreen
import ml.nandor.confusegroups.presentation.decks.DecksScreen
import ml.nandor.confusegroups.presentation.MainViewModel
import ml.nandor.confusegroups.presentation.ManualConfusionsScreen
import ml.nandor.confusegroups.presentation.review.ReviewScreen
import ml.nandor.confusegroups.presentation.SearchAndAddManualPopup
import ml.nandor.confusegroups.presentation.cards.CardsScreen
import ml.nandor.confusegroups.presentation.xport.XportScreen
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
                val commonViewModel: CommonViewModel = hiltViewModel()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when(commonViewModel.selectedMode.value){
                        CommonViewModel.DeckOpenMode.NONE -> DecksScreen(commonViewModel)
                        CommonViewModel.DeckOpenMode.REVIEW -> ReviewScreen(playNoise, commonViewModel)
                        CommonViewModel.DeckOpenMode.CORRELATIONS -> ComparisonScreen(commonViewModel)
                        CommonViewModel.DeckOpenMode.CONFUSEGROUPS -> ManualConfusionsScreen(viewModel = viewModel, commonViewModel)
                        CommonViewModel.DeckOpenMode.VIEWCARDS -> CardsScreen(commonViewModel)
                        CommonViewModel.DeckOpenMode.XPORT -> XportScreen(commonViewModel = commonViewModel)

                    }

                    val popups = viewModel.comparisonPopups.value
                    Box {
                        for (popup in popups){
                            ComparisonPopup(text = popup, viewModel)
                        }
                    }

                    SearchAndAddManualPopup(commonViewModel)

                }
            }
        }
    }






}