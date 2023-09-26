package ml.nandor.confusegroups.domain.usecase

import android.util.Log
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

// this is where the most important part of the app happens
class GetViewablesFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
): UseCase<String?, List<PreparedViewableCard>>() {
    override fun doStuff(deckName: String?): List<PreparedViewableCard> {

        val allCards = repository.getCardsByDeckName(deckName)

        Timber.d("With ${allCards.size} cards in deck $deckName")

        // if we don't have enough cards for 4 Backs, we can't review
        if (deckName == null || allCards.size < 4){
            throw(Exception("Empty deck"))
        }

        val deck = repository.getDeckByName(deckName)

        val reviews = repository.getMostRecentReviewsByDeckName(deckName)

        val allReviews = repository.listReviews()

        val viewAbles = allCards.map {note ->
            val possiblesWrongs = allCards
                .filter { it.question != note.question && it.answer != note.answer }
                .map { it -> it.answer }
                .toMutableList()

            if (possiblesWrongs.size < 3){
                throw(Exception("Not enough options"))
            }

            val answers = possiblesWrongs.shuffled().take(3).toMutableList()

            answers.add(note.answer)

            val options = answers.shuffled()

            val streakSoFar = reviews.find { it.question == note.question }?.streak ?: 0

            val viewableCard = PreparedViewableCard(note.question, options.indexOf(note.answer)+1, options, streakSoFar)

            return@map viewableCard

        }

        val reviewDates = reviews.map {
            Pair(it,
                it.level+deck.successMultiplier.pow(it.streak).toInt())
        }

        val level = if (reviewDates.isEmpty()) 1 else reviewDates.map { it.second }.min()

        val final = viewAbles.filter {card ->
            val mostRecentReview = reviewDates.find { it.first.question == card.front}

            if (mostRecentReview == null){
                return@filter true; //todo compare to deck new card limit
            }

            val cardLevel = mostRecentReview.first.level+deck.successMultiplier.pow(mostRecentReview.first.streak).toInt()

            return@filter cardLevel <= level

        }.shuffled()

        return final

    }
}