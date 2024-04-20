package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject
import kotlin.math.pow

class GetLevelOfMultipleDecksUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<List<Deck>?, Map<String, Int>>() {
    override fun doStuff(input: List<Deck>?): Map<String, Int> {
        if (input == null){
            return mapOf()
        }

        return input.associate { deck ->

            val reviews = repository.getMostRecentReviewsByDeckName(deck.name)

            val reviewDates = reviews.map {
                it.level+deck.successMultiplier.pow(it.streak).toInt()
            }

            val levelOfThisDeck = if (reviewDates.isEmpty()) 1 else reviewDates.min()

            deck.name to levelOfThisDeck
        }

    }
}