package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

// this is where the most important part of the app happens
class GetAllCorrelationsUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
): UseCase<String?, List<Correlation>>() {
    override fun doStuff(deckName: String?): List<Correlation> {

        val startTime = System.currentTimeMillis()

        val allCards = repository.getCardsByDeckName(deckName)

        val allReviews = repository.listReviews()

        val correlations = mutableListOf<Correlation>()

        val expontent = 1.0

        // associate makes a map of key-value pairs
        val reviewsByLeft = allCards.associate { it -> Pair(it.id, mutableListOf<Review>()) }

        for (review in allReviews){
            reviewsByLeft[review.question]?.add(review)
        }

        allCards.forEachIndexed{index, leftCard ->
            for (rightCard in allCards){
                if (leftCard.answer == rightCard.answer)
                    continue

                val corr = determineCorrelation(leftCard.id, rightCard.answer, 1.0, reviewsByLeft[leftCard.id]!!.toList())

                correlations.add(Correlation(leftCard, rightCard, corr))
            }
            if (index % 100 == 0)
                Timber.d("${index}/${allCards.size}")
        }

        val final = correlations.sortedBy { -it.correlation }.take(100).map { it->it.copy(correlation = it.correlation.pow(expontent)) }

        val endTime = System.currentTimeMillis()

        Timber.d("${endTime-startTime}")

        return final

    }

    private fun determineCorrelation(question: String, answer: String, exponent: Double, reviews: List<Review>):Double{

        // currently ignoring reverse reviews
        val relevants = reviews.filter { it.question == question }

        val mistakeCount = relevants.filter { it.answer == answer }.size
        val correctCount = relevants.filter { answer in it.unpickedAnswers.split(";") }.size

        val totalCount = mistakeCount+correctCount

        // the magic formula that makes it all work
        val base = (mistakeCount+0.5)/(totalCount+1.0)/2.0

        //val final = base.pow(exponent)

        return base
    }
}