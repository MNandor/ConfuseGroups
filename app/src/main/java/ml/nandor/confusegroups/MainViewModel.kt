package ml.nandor.confusegroups

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var index: Int = 0

    fun currentQuestion():CardData {
        return hardCoded[index]
    }
    fun checkAnswer(answer:String):Boolean{
        val question = hardCoded[index]
        nextQuestion()
        return answer == question.options[question.correct-1]
    }

    private fun nextQuestion(){
        index += 1
    }

}