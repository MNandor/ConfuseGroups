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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo:LocalStorageRepository
): ViewModel() {
    private val hardCoded: List<PreparedViewableCard> = listOf(
        PreparedViewableCard("人", 4, listOf("Dog", "Big", "Bow", "Person")),
        PreparedViewableCard("日", 1, listOf("Day", "Eye", "Sun", "Month")), // Correct answer in slot 1
        PreparedViewableCard("食", 1, listOf("Eat", "Drink", "Fly", "Hunger")), // Correct answer in slot 1
        PreparedViewableCard("木", 3, listOf("Forest", "Book", "Tree", "Wood")), // Correct answer in slot 4
        PreparedViewableCard("水", 2, listOf("Ice", "Water", "Fire", "Flame"))  // Correct answer in slot 3
    )

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
    val currentQuestion = derivedStateOf { hardCoded[_currentIndex.value]}

    fun checkAnswer(answer:String):Boolean{
        val question = hardCoded[_currentIndex.value]

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
                if (_currentIndex.value >= hardCoded.size) {
                    _currentIndex.value = 0
                }
                _cardCorrectness.value = allCorrect
            }
        } else {
            _currentIndex.value += 1
            if (_currentIndex.value >= hardCoded.size) {
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

        val deck = Deck(name = name, 0, 0.0, 0.0, 0)

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

}