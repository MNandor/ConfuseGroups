package ml.nandor.confusegroups.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import ml.nandor.confusegroups.domain.usecase.GetCardsFromDeckUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo:LocalStorageRepository,
    private val getCardsFromDeckUseCase: GetCardsFromDeckUseCase
): ViewModel() {

    private val _hardCoded:MutableState<List<PreparedViewableCard>> = mutableStateOf(listOf())
    val hardCoded:State<List<PreparedViewableCard>> = _hardCoded
    enum class CardCorrectness{
        BASE,
        GOOD,
        BAD
    }

    private val allCorrect = listOf(
        CardCorrectness.BASE,
        CardCorrectness.BASE,
        CardCorrectness.BASE,
        CardCorrectness.BASE
    )
    private val _cardCorrectness = mutableStateOf(allCorrect)
    val cardCorrectness = _cardCorrectness

    private val _currentIndex = mutableStateOf(0)
    val currentQuestion = derivedStateOf { hardCoded.value[_currentIndex.value]}

    fun checkAnswer(answer:String):Boolean{
        val question = hardCoded.value[_currentIndex.value]

        if (answer == question.options[question.correct-1]){
            nextQuestion(false)
            return true
        } else {
            val cors:MutableList<CardCorrectness> = mutableListOf()

            for (option in question.options){
                if (option == answer)
                    cors.add(CardCorrectness.BAD)
                else if (option == question.options[question.correct-1])
                    cors.add(CardCorrectness.GOOD)
                else
                    cors.add(CardCorrectness.BASE)

                _cardCorrectness.value = cors
            }

            nextQuestion(true)

            return false
        }
    }

    private fun nextQuestion(delay:Boolean = false){
        if (delay){
            viewModelScope.launch {
                delay(3000)
                _currentIndex.value += 1
                if (_currentIndex.value >= hardCoded.value.size) {
                    _currentIndex.value = 0
                }
                _cardCorrectness.value = allCorrect
            }
        } else {
            _currentIndex.value += 1
            if (_currentIndex.value >= hardCoded.value.size) {
                _currentIndex.value = 0
            }
        }

    }

    private val _selectedDeck: MutableState<String?> = mutableStateOf(null)
    val selectedDeck = _selectedDeck

    fun selectDeck(deckName: String?){
        _selectedDeck.value = deckName
    }

    fun createDeck(){
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val name = (1..8).map { chars.random() }.joinToString(separator = "")

        val deck = Deck(name = name, 10, 2.0, 1.0, 0)

        viewModelScope.launch {
            repo.insertDeck(deck)
            updateDecks()
        }


    }

    private val _decks:MutableState<List<Deck>> = mutableStateOf(listOf())
    val decks = _decks


    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    init {
        updateDecks()

        getCardsFromDeckUseCase(Unit).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _hardCoded.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateDecks(){
        // not on main thread
        viewModelScope.launch {
            // access database
            val loadedDecks = repo.listDecks()
            // return to main thread to update state
            withContext(Dispatchers.Main) {
                _decks.value = loadedDecks
            }
        }
    }

    private val _deckBeingDeleted:MutableState<String?> = mutableStateOf(null)
    val deckBeingDeleted:State<String?> = _deckBeingDeleted
    fun enterDeleteMode(deckName: String?){
        _deckBeingDeleted.value = deckName
    }

    fun deleteDeck(){
        if (deckBeingDeleted.value != null){
            viewModelScope.launch {
                repo.deleteDeckByName(deckBeingDeleted.value!!)
                enterDeleteMode(null)
                updateDecks()
            }
        }
    }

    private val _deckBeingEdited:MutableState<String?> = mutableStateOf(null)
    val deckBeingEdited:State<String?> = _deckBeingEdited

    private val _editedDeckState:MutableState<Deck?> = mutableStateOf(null)
    val editedDeckState:State<Deck?> = _editedDeckState

    fun enterEditMode(deckName: String?){
        _deckBeingEdited.value = deckName

        _editedDeckState.value = decks.value.find { it.name == deckName }
    }

    private val _deckBeingAddedTo:MutableState<String?> = mutableStateOf(null)
    val deckBeingAddedTo:State<String?> = _deckBeingAddedTo
    fun enterAddMode(deckName: String?){
        _deckBeingAddedTo.value = deckName

    }

    fun addCard(card: AtomicNote){
        viewModelScope.launch {
            repo.insertCard(card)
        }
    }

    private val _deckBeingInspected:MutableState<String?> = mutableStateOf(null)
    val deckBeingInspected:State<String?> = _deckBeingInspected



    private val _inspectedDeckCards:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

    val inspectedDeckCards:State<List<AtomicNote>> = _inspectedDeckCards
    fun enterInspectMode(deckName: String?){
        _deckBeingInspected.value = deckName

        viewModelScope.launch {
            val cards = repo.getCardsByDeckName(deckName)
            withContext(Dispatchers.Main) {
                _inspectedDeckCards.value = cards
            }
        }
    }
}