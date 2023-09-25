package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject
import kotlin.math.pow

class GetLevelOfDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String?, Int>() {
    override fun doStuff(input: String?): Int {
        if (input == null){
            return 0;
        }

        // the level of a deck is the lowest level review available to us

        val reviews = repository.getMostRecentReviewsByDeckName(input)
        val deck = repository.getDeckByName(input)

        val reviewDates = reviews.map {
            it.level+deck.successMultiplier.pow(it.streak).toInt()
        }

        if (reviewDates.isEmpty()){
            return 1;
        }

        return reviewDates.min()

    }
}