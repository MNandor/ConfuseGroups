package ml.nandor.confusegroups.presentation.review

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
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.usecase.CreateConfuseGroupWithAnotherCardUseCase
import ml.nandor.confusegroups.domain.usecase.GetAllCorrelationsUseCase
import ml.nandor.confusegroups.domain.usecase.GetLevelOfDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetQuestionFromAnswerUseCase
import ml.nandor.confusegroups.domain.usecase.GetViewablesFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.InsertNewReviewUseCase
import ml.nandor.confusegroups.domain.usecase.InsertReviewUseCase
import ml.nandor.confusegroups.domain.usecase.JoinConfuseGroupUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsGroupedFromDeckUseCase
import ml.nandor.confusegroups.presentation.MainViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
    private val getViewablesFromDeckUseCase: GetViewablesFromDeckUseCase,
    private val insertReviewUseCase: InsertReviewUseCase,
    private val getQuestionFromAnswerUseCase: GetQuestionFromAnswerUseCase,
    private val insertNewReviewUseCase: InsertNewReviewUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private var isClickable = true
    fun isClickable():Boolean = isClickable


    private val _currentIndex = mutableStateOf(0)
    val currentQuestion = derivedStateOf {
        if (viewableCards.value.lastIndex >= _currentIndex.value) {
            val card = viewableCards.value[_currentIndex.value]
            Timber.d(">[${_currentIndex.value}] $card")
            card
        } else null
    }

    private val _viewableCards:MutableState<List<PreparedViewableCard>> = mutableStateOf(listOf())
    val viewableCards:State<List<PreparedViewableCard>> = _viewableCards

    private var selectedDeck: String? = null
    fun beginReviewingADeck(deckName: String){
        // don't reload on recompose
        if (selectedDeck == deckName)
            return
        selectedDeck = deckName
        _currentIndex.value = Int.MAX_VALUE

        loadANewLevel()

    }

    fun loadANewLevel(){


        updateDeckLevel(selectedDeck)

        getViewablesFromDeckUseCase(selectedDeck).onEach {
            if (it is Resource.Loading){
                isClickable = false
            }

            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    Timber.d("${it.data!!}")
                    _viewableCards.value = it.data!!
                    _currentIndex.value = 0

                    isClickable = true
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _deckLevel: MutableState<Int> = mutableStateOf(0)
    val deckLevel = _deckLevel
    private fun updateDeckLevel(deckName: String?){
        getLevelOfDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckLevel.value = it.data!!
                    Timber.d("Deck level is ${it.data!!}")
                }
            }

        }.launchIn(viewModelScope)
    }


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

    fun checkAnswer(answer:String):Boolean{
        val card = viewableCards.value[_currentIndex.value]

        val wasCorrect = answer == card.options[card.correct-1]

        val timeStamp = System.currentTimeMillis()

        val review = Review(
            question = card.note.id, //alright this needs to be changed
            answer = answer,
            level = deckLevel.value,
            streak = if (wasCorrect) card.streakSoFar+1 else 0,
            timeStamp = timeStamp,
            unpickedAnswers = card.options.filter { it != answer }.joinToString(";")
        )

        val pickedNote = card.optionsAsNotes.find {
            it.answer == answer
        }
        val unpickedNotes = card.optionsAsNotes.filter {
            it.answer != answer
        }

        Timber.d("Picked $pickedNote")
        unpickedNotes.forEach{
            Timber.d("Not picked $it")
        }

        if (pickedNote!=null){
            val newReview = NewReview(
                timeStamp = timeStamp,
                questionID = card.note.id,
                answerOptionID = pickedNote.id,
                levelWhereThisHappened = deckLevel.value,
                wasThisOptionPicked = true,
                streakValueAfterThis = if (wasCorrect) card.streakSoFar+1 else 0,
            )
            Timber.d("$newReview")
            insertNewReviewUseCase(newReview).launchIn(viewModelScope)
        } else {
            Timber.e("The picked card doesn't exist!")
        }

        for (unpickedNote in unpickedNotes){
            val newReview = NewReview(
                timeStamp = timeStamp,
                questionID = card.note.id,
                answerOptionID = unpickedNote.id,
                levelWhereThisHappened = deckLevel.value,
                wasThisOptionPicked = false,
                streakValueAfterThis = if (wasCorrect) card.streakSoFar+1 else 0,
            )

            Timber.d("$newReview")
            insertNewReviewUseCase(newReview).launchIn(viewModelScope)
        }


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

               updateDeckLevel(selectedDeck)
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
                    loadANewLevel()
                } else {
                    _currentIndex.value += 1
                }
                _cardCorrectness.value = allCorrect
                displayComparison(null)
            }
        } else {
            if (_currentIndex.value >= viewableCards.value.size-1) {
                loadANewLevel()
            } else {
                _currentIndex.value += 1
            }
            displayComparison(null)
        }

    }

    private val _comparisonQuestion:MutableState<String?> = mutableStateOf(null)
    val comparisonQuestion = _comparisonQuestion

    fun displayComparison(answer: String?){
        if (answer == null){
            _comparisonQuestion.value = null
            return
        }

        _comparisonQuestion.value = currentQuestion.value?.optionsAsNotes?.find { it.answer == answer }?.question
    }
}