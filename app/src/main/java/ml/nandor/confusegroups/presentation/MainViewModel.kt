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
    private val listDecksUseCase: ListDecksUseCase,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
    private val insertReviewUseCase: InsertReviewUseCase,
    private val getQuestionFromAnswerUseCase: GetQuestionFromAnswerUseCase,
    private val getDeckSizesUseCase: GetDeckSizesUseCase,
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

    private val _currentIndex = mutableStateOf(0)


    private var isClickable = true



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


    private val _comparisonPopups:MutableState<List<String>> = mutableStateOf(listOf())
    val comparisonPopups:State<List<String>> = _comparisonPopups
    fun addComparisonPopup(text: String){
        _comparisonPopups.value = _comparisonPopups.value.toMutableList()+text
    }

    fun removeComparisonPopup(text: String){
        _comparisonPopups.value = _comparisonPopups.value.filter { it != text }
    }



    private val _manualCardLeft:MutableState<String?> = mutableStateOf(null)
    val manualCardLeft:State<String?> = _manualCardLeft
    fun setManualLeft(cardName: String?){
//        _manualCardLeft.value = cardName
//        setManualRightSearchTerm("")
//        listCardsFromDeckUseCase(selectedDeck.value).onEach {
//            if (it is Resource.Success){
//                withContext(Dispatchers.Main) {
//                    _allCardsForManual.value = it.data!!
//                }
//            }
//        }.launchIn(viewModelScope)
    }

    private val _allCardsForManual:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

    val allCardsForManualFiltered = derivedStateOf {
        _allCardsForManual.value.filter{
            (it.question+" - "+it.answer).lowercase().contains(_manualRightSearchTerm.value.lowercase()) &&
            it.id != manualCardLeft.value &&
            _manualConfusions.value.find { itt -> it.id == itt.leftCard && manualCardLeft.value == itt.rightCard } == null &&
            _manualConfusions.value.find { itt -> it.id == itt.rightCard && manualCardLeft.value == itt.leftCard } == null
        }
    }

    private val _manualRightSearchTerm:MutableState<String> = mutableStateOf("")

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

        var idsAndGroupIndices = _allCardsForManual.value.mapIndexed { index, item -> Pair(item.id, index)}.toMap()

        Timber.d(idsAndGroupIndices.toString())

        for (confusion in _manualConfusions.value){
            if (idsAndGroupIndices[confusion.leftCard] != idsAndGroupIndices[confusion.rightCard]){
                val idToChange = idsAndGroupIndices[confusion.rightCard]!!
                val newId = idsAndGroupIndices[confusion.leftCard]!!
                idsAndGroupIndices = idsAndGroupIndices.mapValues { if (it.value == idToChange) newId else it.value }
            }
        }

        Timber.d(idsAndGroupIndices.toString())

        val finalList = idsAndGroupIndices.toList().sortedBy { it.second }

        // just filter those that don't belong to any confusegroups
        val groupCounts = finalList.map { it.second }.toSet().toList().map { c -> Pair(c, finalList.count { it.second == c }) }.filter { it.second > 1 }.map { it.first }
        val finalerList = finalList.filter { it.second in groupCounts }

        // terrible code, will be rewritten
        val finalFinalList = finalerList.map { Pair(_allCardsForManual.value.find { itt -> itt.id == it.first }!!, it.second) }

        Timber.d(finalerList.toString())

        return@derivedStateOf finalFinalList
    }

    fun getManualRelationsCount(question: String): Int{
        val pair = allCardsGrouped.value.find { it.first.id == question }
        if (pair == null)
            return 0;

        val count = allCardsGrouped.value.filter { it.second == pair.second }.size

        return count -1;
    }
}