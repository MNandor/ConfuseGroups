package ml.nandor.confusegroups.presentation.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.usecase.GetAllCorrelationsUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val listCardsFromDeckUseCase: ListCardsFromDeckUseCase,
    private val getAllCorrelationsUseCase: GetAllCorrelationsUseCase,
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    // viewmodel shared between all screens
    // mostly just used to keep track of what screen is open
    // and in what way

    private val _selectedDeck: MutableState<String?> = mutableStateOf(null)
    val selectedDeck = _selectedDeck

    fun selectDeck(deckName: String?){
        Timber.d("Selected deck $deckName for review!")
        _selectedDeck.value = deckName
    }


    private val _comparisonDeck:MutableState<String?> = mutableStateOf(null)
    val comparisonDeck: State<String?> = _comparisonDeck
    private val _correlations:MutableState<List<Correlation>> = mutableStateOf(listOf())
    val correlations: State<List<Correlation>> = _correlations
    fun setComparisonDeck(deckName: String?){
        _comparisonDeck.value = deckName
        Timber.d("Selected deck $deckName for correlation comparison!")
        getAllCorrelationsUseCase(deckName).onEach {
            if (it is Resource.Success){
                _correlations.value = it.data!!
            }

        }.launchIn(viewModelScope)
    }

    private val _comparisonDeck2:MutableState<String?> = mutableStateOf(null)
    val comparisonDeck2: State<String?> = _comparisonDeck2
    fun setComparisonDeck2(deckName: String?){
        Timber.d("Selected deck $deckName for manual comparison2!")
        _comparisonDeck2.value = deckName
        listCardsFromDeckUseCase(_comparisonDeck2.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                   // _allCardsForManual.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _comparisonDeck3:MutableState<String?> = mutableStateOf(null)
    val comparisonDeck3: State<String?> = _comparisonDeck3
    fun setComparisonDeck3(deckName: String?){
        Timber.d("Selected deck $deckName for card listing!")
        _comparisonDeck3.value = deckName
        listCardsFromDeckUseCase(_comparisonDeck2.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    // _allCardsForManual.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    // todo rather than 4 deck values, have one deck and one state variable


}