package ml.nandor.confusegroups.domain.usecase

import android.util.Log
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log2
import kotlin.math.pow

// this is where the most important part of the app happens
class GetAllCorrelationsUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
): UseCase<String?, List<Correlation>>() {
    override fun doStuff(deckName: String?): List<Correlation> {

        val allCards = repository.getCardsByDeckName(deckName)

        val allReviews = repository.listReviews()

        val correlations = mutableListOf<Correlation>()

        for (leftCard in allCards){
            for (rightCard in allCards){
                if (leftCard == rightCard)
                    continue

                val corr = determineCorrelation(leftCard.question, rightCard.answer, 1.0, allReviews)

                correlations.add(Correlation(leftCard, rightCard, corr))
            }
        }

        val final = correlations.sortedBy { -it.correlation }.take(100)


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

        val final = base.pow(exponent)

        Timber.tag("alg1math").d("\t$answer - (${mistakeCount}/${totalCount}) - ${(final*100).toInt()}%")

        return final
    }
}