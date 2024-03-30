package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.NewCorrelation
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

class GetNewCorrelationsFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, List<NewCorrelation>>() {
    override fun doStuff(input: String): List<NewCorrelation> {

        val reviews = repository.getNewReviewsFromDeck(input)

        val cards = repository.getCardsByDeckName(input).sortedBy { it.id }

        val map = mutableMapOf<String, NewCorrelation>()

        for(i in 0..(cards.size-1)){
            for (j in (i+1)..(cards.size-1)){
                //Timber.d("${cards[i].id} - ${cards[j].id}")
                map["${cards[i].id} - ${cards[j].id}"] = NewCorrelation(cards[i], cards[j])
            }
        }

        for (review in reviews){
            if (review.questionID == review.answerOptionID)
                continue

            if (review.questionID < review.answerOptionID){
                val key = "${review.questionID} - ${review.answerOptionID}"

                if (key !in map.keys){
                    Timber.e("$key not in map")
                }

                if (review.wasThisOptionPicked){
                    map[key]!!.timesLeftFellForRight += 1
                } else {
                    map[key]!!.timesLeftAvoidedRight += 1
                }
            } else {
                val key = "${review.answerOptionID} - ${review.questionID}"

                if (key !in map.keys){
                    Timber.e("$key not in map")
                }

                if (review.wasThisOptionPicked){
                    map[key]!!.timesRightFellForLeft += 1
                } else {
                    map[key]!!.timesRightAvoidedLeft += 1
                }
            }

        }

        val fin = map.values.sortedByDescending { it.getCorrelationValue() }

        Timber.d(fin.toString())

        return fin.take(100)
    }

}