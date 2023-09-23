package ml.nandor.confusegroups.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ml.nandor.confusegroups.domain.CardData
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {
    private val hardCoded: List<CardData> = listOf(
        CardData("人", 4, listOf("Dog", "Big", "Bow", "Person")),
        CardData("日", 1, listOf("Day", "Eye", "Sun", "Month")), // Correct answer in slot 1
        CardData("食", 1, listOf("Eat", "Drink", "Fly", "Hunger")), // Correct answer in slot 1
        CardData("木", 3, listOf("Forest", "Book", "Tree", "Wood")), // Correct answer in slot 4
        CardData("水", 2, listOf("Ice", "Water", "Fire", "Flame"))  // Correct answer in slot 3
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

}