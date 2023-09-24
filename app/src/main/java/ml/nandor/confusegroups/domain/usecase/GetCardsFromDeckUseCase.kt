package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class GetCardsFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Unit, List<PreparedViewableCard>>() {
    override fun doStuff(input: Unit): List<PreparedViewableCard> {
        val hardCoded: List<PreparedViewableCard> = listOf(
            PreparedViewableCard("人", 4, listOf("Dog", "Big", "Bow", "Person")),
            PreparedViewableCard("日", 1, listOf("Day", "Eye", "Sun", "Month")), // Correct answer in slot 1
            PreparedViewableCard("食", 1, listOf("Eat", "Drink", "Fly", "Hunger")), // Correct answer in slot 1
            PreparedViewableCard("木", 3, listOf("Forest", "Book", "Tree", "Wood")), // Correct answer in slot 4
            PreparedViewableCard("水", 2, listOf("Ice", "Water", "Fire", "Flame"))  // Correct answer in slot 3
        )

        return hardCoded
    }
}