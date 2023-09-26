package ml.nandor.confusegroups.domain.usecase

import android.util.Log
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
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

        Timber.d("***\n\n***")
        Timber.d("With ${allCards.size} cards in deck $deckName...")

        // if we don't have enough cards for 4 Backs, we can't review
        if (deckName == null || allCards.size < 4){
            Timber.d("We cancel the review")
            throw(Exception("Empty deck"))
        }

        val deck = repository.getDeckByName(deckName)

        val reviews = repository.getMostRecentReviewsByDeckName(deckName)

        Timber.d("Of which ${reviews.size} cards have previous revies and ${allCards.size-reviews.size} do not")

        val allReviews = repository.listReviews()

        Timber.d("In a total of ${allReviews.size} reviews")

        val reviewDates = reviews.map {
            Pair(it,
                recentReviewToLevel(it, deck.successMultiplier))
        }

        val level = if (reviewDates.isEmpty()) 1 else reviewDates.map { it.second }.min()

        val reviewCards = allCards.filter {card ->
            val mostRecentReview = reviewDates.find { it.first.question == card.question}

            if (mostRecentReview == null){
                return@filter false;
            }

            val cardLevel = mostRecentReview.second

            return@filter cardLevel <= level

        }

        val newCards = allCards.filter {
            card -> val mostRecentReview = reviewDates.find { it.first.question == card.question}

            if (mostRecentReview == null)
                return@filter true

            return@filter false
        }

        Timber.d("The level is $level, which will show ${reviewCards.size} review cards and ${newCards.size}/${deck.newCardsPerLevel} new cards")

        val filteredCards = reviewCards+newCards

        val viewAbles = reviewCards.map {note ->
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

        }.shuffled()

        val newViewables = newCards.map{note ->
            val viewableCard = PreparedViewableCard(note.question, 1, listOf(note.answer), -1)

            return@map viewableCard
        }.shuffled().take(deck.newCardsPerLevel)




        return viewAbles+newViewables

    }

    // Given a review that is known to be most recent
    // Calculate the earliest level the card will appear on again
    private fun recentReviewToLevel(review: Review, deckMultiplier:Double):Int{
        return review.level+deckMultiplier.pow(review.streak).toInt()
    }
}