package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log2
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

        Timber.tag("alg1time").d("***\n\n***")

        // associate makes a map of key-value pairs
        val reviewsByLeft = allCards.associate { it -> Pair(it.question, mutableListOf<Review>()) }

        for (review in allReviews){
            reviewsByLeft[review.question]?.add(review)
        }

        Timber.tag("alg1math").d("***\n\n***")
        val viewAbles = reviewCards.map {note ->
            val possiblesWrongs = allCards
                .filter { it.question != note.question && it.answer != note.answer }
                .map { it -> it.answer }
                .toMutableList()

            Timber.tag("alg1math").d("${note.question}")

            val corres = possiblesWrongs
                .shuffled()
                .map { pos ->  Pair(pos, -determineCorrelation(note.question, pos, deck.confuseExponent, reviewsByLeft[note.question]!!.toList())) }
                .sortedBy { it.second }
                .map{it.first}

            val answers = corres.take(2).toMutableList()

            // always have a full random option
            val fullRandom = possiblesWrongs.filter { !answers.contains(it) }.random()


            answers.add(note.answer)
            answers.add(fullRandom)

            val options = answers.shuffled()

            // known to be not null, otherwise it'd be a new card
            val streakSoFar = reviews.find { it.question == note.question }!!.streak

            val viewableCard = PreparedViewableCard(note.question, options.indexOf(note.answer)+1, options, streakSoFar)

            return@map viewableCard

        }.shuffled()
        Timber.tag("alg1math").d("***\n\n***")

        Timber.tag("alg1time").d("***\n\n***")

        val newCount = if (deck.newCardsPerLevel == -1) log2(allCards.size.toDouble()).toInt() else deck.newCardsPerLevel

        Timber.d("Limiting to $newCount new cards")

        val newViewables = newCards.map{note ->
            val viewableCard = PreparedViewableCard(note.question, 1, listOf(note.answer), -1)

            return@map viewableCard
        }.shuffled().take(newCount)




        return viewAbles+newViewables

    }

    // Given a review that is known to be most recent
    // Calculate the earliest level the card will appear on again
    private fun recentReviewToLevel(review: Review, deckMultiplier:Double):Int{
        return review.level+deckMultiplier.pow(review.streak).toInt()
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

        //Timber.tag("alg1math").d("\t$answer - (${mistakeCount}/${totalCount}) - ${(final*100).toInt()}%")

        return final
    }
}