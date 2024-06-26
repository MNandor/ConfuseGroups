package ml.nandor.confusegroups.presentation.cards

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.usecase.AddCardsFromTextUseCase
import ml.nandor.confusegroups.domain.usecase.DeleteDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetDeckSizesUseCase
import ml.nandor.confusegroups.domain.usecase.InsertCardUseCase
import ml.nandor.confusegroups.domain.usecase.InsertDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListDecksUseCase
import ml.nandor.confusegroups.domain.usecase.RenameDeckUseCase
import ml.nandor.confusegroups.domain.usecase.UpdateCardUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val listCardsFromDeckUseCase: ListCardsFromDeckUseCase,
    private val updateCardUseCase: UpdateCardUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    var deckCards:  List<AtomicNote> by mutableStateOf(listOf())
        private set


    var currentDeck = ""


    // used to avoid a bug
    // where the UI would load data from the previously selected deck
    private val _readyToCompose = mutableStateOf(false)
    val readyToCompose = _readyToCompose
    fun loadCardsFromDatabase(deckName: String){
        _readyToCompose.value=false
        currentDeck = deckName
        listCardsFromDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    deckCards = it.data!!
                    _readyToCompose.value=true
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateCardValues(id: String, question: String?, answer: String){
        Timber.d("Updating card [[$id]]")
        val note = AtomicNote(id, answer, "", question)
        updateCardUseCase(note).onEach {
            Timber.d("Updated card [[$id]]")
            loadCardsFromDatabase(currentDeck)
        }.launchIn(viewModelScope)
    }

}