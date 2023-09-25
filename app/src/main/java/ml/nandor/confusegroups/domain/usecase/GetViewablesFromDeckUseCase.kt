package ml.nandor.confusegroups.domain.usecase

import android.util.Log
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject
import kotlin.math.pow

class GetViewablesFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val getLevelOfDeckUseCase: GetLevelOfDeckUseCase,
): UseCase<String?, List<PreparedViewableCard>>() {
    override fun doStuff(deckName: String?): List<PreparedViewableCard> {

        val allCards = repository.getCardsByDeckName(deckName)

        if (deckName == null || allCards.size < 4){
            throw(Exception("Empty deck"))
        }

        val deck = repository.getDeckByName(deckName)

        val reviews = repository.getMostRecentReviewsByDeckName(deckName)

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

            val viewableCard = PreparedViewableCard(note.question, options.indexOf(note.answer)+1, options)

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