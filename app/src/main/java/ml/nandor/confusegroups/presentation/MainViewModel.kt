package ml.nandor.confusegroups.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.usecase.AddCardsFromTextUseCase
import ml.nandor.confusegroups.domain.usecase.DeleteDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetAllCorrelationsUseCase
import ml.nandor.confusegroups.domain.usecase.GetDeckSizesUseCase
import ml.nandor.confusegroups.domain.usecase.GetLevelOfDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetQuestionFromAnswerUseCase
import ml.nandor.confusegroups.domain.usecase.GetViewablesFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.InsertCardUseCase
import ml.nandor.confusegroups.domain.usecase.InsertDeckUseCase
import ml.nandor.confusegroups.domain.usecase.InsertManualConfusionUseCase
import ml.nandor.confusegroups.domain.usecase.InsertReviewUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListDecksUseCase
import ml.nandor.confusegroups.domain.usecase.ListManualConfusionsUseCase
import ml.nandor.confusegroups.domain.usecase.RenameDeckUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getViewablesFromDeckUseCase: GetViewablesFromDeckUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val listDecksUseCase: ListDecksUseCase,
    private val insertDeckUseCase: InsertDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val listCardsFromDeckUseCase: ListCardsFromDeckUseCase,
    private val addCardsFromTextUseCase: AddCardsFromTextUseCase,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
    private val insertReviewUseCase: InsertReviewUseCase,
    private val getQuestionFromAnswerUseCase: GetQuestionFromAnswerUseCase,
    private val getAllCorrelationsUseCase: GetAllCorrelationsUseCase,
    private val getDeckSizesUseCase: GetDeckSizesUseCase,
    private val renameDeckUseCase: RenameDeckUseCase,
    private val insertManualConfusionUseCase: InsertManualConfusionUseCase,
    private val listManualConfusionsUseCase: ListManualConfusionsUseCase
): ViewModel() {

    private val _viewableCards:MutableState<List<PreparedViewableCard>> = mutableStateOf(listOf())
    val viewableCards:State<List<PreparedViewableCard>> = _viewableCards
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
    val currentQuestion = derivedStateOf { viewableCards.value[_currentIndex.value]}

    private var isClickable = true
    fun isClickable():Boolean = isClickable
    fun checkAnswer(answer:String):Boolean{
        val card = viewableCards.value[_currentIndex.value]

        val wasCorrect = answer == card.options[card.correct-1]

        val review = Review(
            question = card.note.id, //alright this needs to be changed
            answer = answer,
            level = deckLevel.value,
            streak = if (wasCorrect) card.streakSoFar+1 else 0,
            timeStamp = System.currentTimeMillis()/1000,
            unpickedAnswers = card.options.filter { it != answer }.joinToString(";")
        )

        insertReviewUseCase(review).onEach {
            if (it is Resource.Success){
                if (wasCorrect){
                    nextQuestion(false)
                } else {
                    val cors:MutableList<CardCorrectness> = mutableListOf()

                    for (option in card.options){
                        if (option == answer)
                            cors.add(CardCorrectness.BAD)
                        else if (option == card.options[card.correct-1])
                            cors.add(CardCorrectness.GOOD)
                        else
                            cors.add(CardCorrectness.BASE)

                        _cardCorrectness.value = cors
                    }

                    nextQuestion(true)
                }

                updateDeckLevel(selectedDeck.value)
            }

        }.launchIn(viewModelScope)

        return wasCorrect
    }

    fun nextQuestion(delay:Boolean = false){
        if (delay){
            viewModelScope.launch {
                isClickable = false
                delay(3000)
                isClickable = true

                if (_currentIndex.value >= viewableCards.value.size-1) {
                    selectDeck(selectedDeck.value)
                } else {
                    _currentIndex.value += 1
                }
                _cardCorrectness.value = allCorrect
                displayComparison(null)
            }
        } else {
            if (_currentIndex.value >= viewableCards.value.size-1) {
                selectDeck(selectedDeck.value)
            } else {
                _currentIndex.value += 1
            }
            displayComparison(null)
        }

    }

    private val _selectedDeck: MutableState<String?> = mutableStateOf(null)
    val selectedDeck = _selectedDeck

    private val _deckLevel: MutableState<Int> = mutableStateOf(0)
    val deckLevel = _deckLevel
    private fun updateDeckLevel(deckName: String?){
        getLevelOfDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckLevel.value = it.data!!
                }
            }

        }.launchIn(viewModelScope)
    }
    fun selectDeck(deckName: String?){
        updateDeckLevel(deckName)
        getViewablesFromDeckUseCase(deckName).onEach {
            if (it is Resource.Loading){
                isClickable = false
            }

            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _currentIndex.value = 0
                    _viewableCards.value = it.data!!
                    _selectedDeck.value = deckName
                    isClickable = true
                }
            } else if (it is Resource.Error){
                withContext(Dispatchers.Main) {
                    _selectedDeck.value = null
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createDeck(){
        val name = Util.getDeckName()

        val deck = Deck(name = name, -1, 1.5, 1.0, 0)


        insertDeckUseCase(deck).onEach {
            if (it is Resource.Success)
                updateDecks()
        }.launchIn(viewModelScope)

    }

    private val _decks:MutableState<List<Deck>> = mutableStateOf(listOf())
    val decks = _decks


    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    init {
        updateDecks()
        updateManualConfusions()
    }

    private fun updateDecks(){
        listDecksUseCase(Unit).onEach {
            if (it is Resource.Success){
                // return to main thread to update state
                withContext(Dispatchers.Main) {
                    _decks.value = it.data!!
                }

                updateDeckSizes()
            }

        }.launchIn(viewModelScope)
    }

    private fun updateDeckSizes(){
        getDeckSizesUseCase(Unit).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckSizes.value = it.data!!.associate { Pair(it.name, it.count) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getDeckSize(deckName: String): Int {
        return _deckSizes.value[deckName] ?: 0
    }

    private val _deckSizes = mutableStateOf<Map<String, Int>>(mapOf())

    private val _deckBeingAccessed:MutableState<String?> = mutableStateOf(null)
    val deckBeingAccessed:State<String?> = _deckBeingAccessed

    enum class DeckAction{
        DELETION,
        EDITING,
        INSPECTION,
        ADDING,
        RENAME
    }

    private val _deckActionBeingTaken:MutableState<DeckAction?> = mutableStateOf(null)
    val deckActionBeingTaken:State<DeckAction?> = _deckActionBeingTaken

    fun enterDeckActionMode(deckName:String? = null, mode:DeckAction? = null){
        _deckBeingAccessed.value = deckName
        _deckActionBeingTaken.value = mode

        when(mode){
            DeckAction.EDITING -> {
                _editedDeckState.value = decks.value.find { it.name == deckName }
            }
            DeckAction.INSPECTION -> {
                listCardsFromDeckUseCase(deckName).onEach {
                    if (it is Resource.Success){
                        withContext(Dispatchers.Main) {
                            _inspectedDeckCards.value = it.data!!
                        }
                    }
                }.launchIn(viewModelScope)

            }
            else -> {}
        }

    }
    fun deleteDeck(){
        if (_deckBeingAccessed.value != null){
            deleteDeckUseCase(_deckBeingAccessed.value!!).onEach {
                if (it is Resource.Success){
                    updateDecks()
                }
            }.launchIn(viewModelScope)
            enterDeckActionMode(null)
        }
    }

    private val _editedDeckState:MutableState<Deck?> = mutableStateOf(null)
    val editedDeckState:State<Deck?> = _editedDeckState


    fun addCard(card: AtomicNote){
        insertCardUseCase(card).launchIn(viewModelScope)
    }

    private val _inspectedDeckCards:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

    val inspectedDeckCards:State<List<AtomicNote>> = _inspectedDeckCards

    fun loadDeckFromText(text: String){
        addCardsFromTextUseCase(Pair(_deckBeingAccessed.value!!, text)).launchIn(viewModelScope)
    }

    private val _comparisonQuestion:MutableState<String?> = mutableStateOf(null)
    val comparisonQuestion = _comparisonQuestion
    fun displayComparison(answer: String?){
        if (answer == null){
            _comparisonQuestion.value = null
            return
        }

        getQuestionFromAnswerUseCase(answer).onEach {
            if (it is Resource.Success){
                _comparisonQuestion.value = it.data
            }
        }.launchIn(viewModelScope)
    }

    private val _comparisonDeck:MutableState<String?> = mutableStateOf(null)
    val comparisonDeck:State<String?> = _comparisonDeck
    private val _correlations:MutableState<List<Correlation>> = mutableStateOf(listOf())
    val correlations:State<List<Correlation>> = _correlations
    fun setComparisonDeck(deckName: String?){
        _comparisonDeck.value = deckName
        getAllCorrelationsUseCase(deckName).onEach {
            if (it is Resource.Success){
                _correlations.value = it.data!!
            }

        }.launchIn(viewModelScope)
    }

    private val _comparisonDeck2:MutableState<String?> = mutableStateOf(null)
    val comparisonDeck2:State<String?> = _comparisonDeck2
    fun setComparisonDeck2(deckName: String?){
        _comparisonDeck2.value = deckName
        listCardsFromDeckUseCase(_comparisonDeck2.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _allCardsForManual.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _comparisonPopups:MutableState<List<String>> = mutableStateOf(listOf())
    val comparisonPopups:State<List<String>> = _comparisonPopups
    fun addComparisonPopup(text: String){
        _comparisonPopups.value = _comparisonPopups.value.toMutableList()+text
    }

    fun removeComparisonPopup(text: String){
        _comparisonPopups.value = _comparisonPopups.value.filter { it != text }
    }

    fun renameDeck(deckName:String, deckDisplayName: String){
        renameDeckUseCase(Pair(deckName, deckDisplayName)).onEach {
            if (it is Resource.Success){
                updateDecks()
            }
        }.launchIn(viewModelScope)
    }

    private val _manualCardLeft:MutableState<String?> = mutableStateOf(null)
    val manualCardLeft:State<String?> = _manualCardLeft
    fun setManualLeft(cardName: String?){
        _manualCardLeft.value = cardName
        setManualRightSearchTerm("")
        listCardsFromDeckUseCase(selectedDeck.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _allCardsForManual.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _allCardsForManual:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

    val allCardsForManualFiltered = derivedStateOf {
        _allCardsForManual.value.filter{
            (it.id+" - "+it.answer).lowercase().contains(_manualRightSearchTerm.value.lowercase()) &&
            it.id != manualCardLeft.value &&
            _manualConfusions.value.find { itt -> it.id == itt.leftCard && manualCardLeft.value == itt.rightCard } == null &&
            _manualConfusions.value.find { itt -> it.id == itt.rightCard && manualCardLeft.value == itt.leftCard } == null
        }
    }

    private val _manualRightSearchTerm:MutableState<String> = mutableStateOf("")
    val manualRightSearchTerm:State<String> = _manualRightSearchTerm
    fun setManualRightSearchTerm(searchTerm: String){
        _manualRightSearchTerm.value = searchTerm
    }

    fun addManualConfusion(left: String, right:String){
        insertManualConfusionUseCase(ManualConfusion(leftCard = left, rightCard = right)).onEach {
            updateManualConfusions()
        }.launchIn(viewModelScope)
    }

    private val _manualConfusions:MutableState<List<ManualConfusion>> = mutableStateOf(listOf())
    private fun updateManualConfusions(){
        listManualConfusionsUseCase(Unit).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main){
                    _manualConfusions.value = it.data!!
                }
                Timber.d(it.data!!.toString())
            }
        }.launchIn(viewModelScope)
    }

    val allCardsGrouped = derivedStateOf {

        var mappedList = _allCardsForManual.value.mapIndexed { index, item -> Pair(item.id, index)}.toMap()

        Timber.d(mappedList.toString())

        for (confusion in _manualConfusions.value){
            if (mappedList[confusion.leftCard] != mappedList[confusion.rightCard]){
                val idToChange = mappedList[confusion.rightCard]!!
                val newId = mappedList[confusion.leftCard]!!
                mappedList = mappedList.mapValues { if (it.value == idToChange) newId else it.value }
            }
        }

        Timber.d(mappedList.toString())

        val finalList = mappedList.toList().sortedBy { it.second }

        val groupCounts = finalList.map { it.second }.toSet().toList().map { c -> Pair(c, finalList.count { it.second == c }) }.filter { it.second > 1 }.map { it.first }

        val finalerList = finalList.filter { it.second in groupCounts }

        Timber.d(finalerList.toString())

        return@derivedStateOf finalerList
    }

    fun getManualRelationsCount(question: String): Int{
        val pair = allCardsGrouped.value.find { it.first == question }
        if (pair == null)
            return 0;

        val count = allCardsGrouped.value.filter { it.second == pair.second }.size

        return count -1;
    }
}