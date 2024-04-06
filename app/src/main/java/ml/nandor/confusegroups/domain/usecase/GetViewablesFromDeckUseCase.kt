package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.BuildConfig
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.NewReview
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

        // for each card, aside from the correct answer, we want to select 3 incorrect options to show the user
        // we prefer selecting more challenging options
        // these are correlated cards; cards that belong to at least one shared group
        // and implicitly: cards we don't know the correlatio nwith
        val CORRELATION_MULTIPLIER = 1
        val GROUP_MULTIPLIER = 1
        val RANDOMNESS_MULTIPLIER = 1

        // todo keeping these as constants for now
        // todo choosing cards per level and chosing wrong answers can be two separate calls
        // allowing for the latter to happen per-card

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

        Timber.d("Of which ${reviews.size} cards have previous reviews and ${allCards.size-reviews.size} do not")

        val allReviews = repository.listReviews()

        Timber.d("In a total of ${allReviews.size} reviews")

        val reviewDates = reviews.map {
            Pair(it,
                recentReviewToLevel(it, deck.successMultiplier))
        }

        val level = getDeckLevel(deckName, deck.successMultiplier)

        // new cards

        val newCards = allCards.filter {
            card -> val mostRecentReview = reviewDates.find { it.first.question == card.id}

            if (mostRecentReview == null)
                return@filter true

            return@filter false
        }

        val newCount = if (deck.newCardsPerLevel == -1) log2(allCards.size.toDouble()).toInt() else deck.newCardsPerLevel

        val newViewables = getNewViewables(newCount, newCards)

        // review cards

        val reviewCards = allCards.filter {card ->
            val mostRecentReview = reviewDates.find { it.first.question == card.id}

            if (mostRecentReview == null){
                return@filter false;
            }

            val cardLevel = mostRecentReview.second

            return@filter cardLevel <= level

        }

        Timber.i("The level is $level, which will show ${reviewCards.size} review cards and ${newCards.size}/${deck.newCardsPerLevel} new cards")
        if (deck.newCardsPerLevel == -1) {
            Timber.d("The cap is auto-set to ${newCount} based on deck size")
        }

        val groupMemberships = repository.listGroupMemberships()

        val newReviews = repository.getNewReviewsFromDeck(deckName)


        val before = System.currentTimeMillis()
        // someday, someone will optimize this
        // because currently it's like O(N^a billion)
        // for now I just want to get it working

        // (performance)
        val reviewCardIDs = reviewCards.map { it.id }
        val maybeRelevantReviews = newReviews.filter { it.questionID in reviewCardIDs || it.answerOptionID in reviewCardIDs }

        val randomOptions = reviewCards.associate { left ->

            val relevantGroupMemberships = groupMemberships
                .filter { it.cardID == left.id } // for each group the left belongs in
                .map { g-> groupMemberships.filter { it.groupID == g.groupID }  } // get all members that belong to the same group

            Pair(left.id, // for each "left" (question note)

                allCards.associate {right ->  // evaluate each "right" (potential answer note)

                    // (performance) select relevant reviews rather than looping through all of them multiple times
                    val relevantReviews = maybeRelevantReviews.filter { (it.questionID == left.id && it.answerOptionID == right.id) ||  (it.questionID == right.id && it.answerOptionID == left.id)}

                    var timesLeftAvoidedRight: Int = relevantReviews.filter { (it.questionID == left.id && it.answerOptionID == right.id && !it.wasThisOptionPicked) }.size
                    var timesRightAvoidedLeft: Int = relevantReviews.filter { (it.questionID == right.id && it.answerOptionID == left.id && !it.wasThisOptionPicked) }.size
                    var timesLeftFellForRight: Int = relevantReviews.filter { (it.questionID == left.id && it.answerOptionID == right.id && it.wasThisOptionPicked) }.size
                    var timesRightFellForLeft: Int = relevantReviews.filter { (it.questionID == right.id && it.answerOptionID == left.id && it.wasThisOptionPicked) }.size

                    val ll = (timesLeftFellForRight+1).toDouble()/(timesLeftAvoidedRight+1)
                    val rr = (timesRightFellForLeft+1).toDouble()/(timesRightAvoidedLeft+1)

                    val correlationValue =  (ll+rr)/2.0f

                    Pair(right.id,
                        object {
                            val hasGroupInCommon = relevantGroupMemberships
                                .map { it.filter { it.cardID == right.id } } // and find if any of the members are the right
                                .filter { it.isNotEmpty() } // *sigh* remove empty groups of groups
                                .isNotEmpty()
                            val isSame = left.id == right.id

                            val correlationValue = correlationValue
                        }
                    )
                }.filter { !it.value.isSame }
            )
        }

        val after = System.currentTimeMillis()
        Timber.i("Lasted ${after-before}ms")

        if (BuildConfig.DEBUG){
            Timber.i("The map!")
            for (left in randomOptions.keys){
                Timber.i(left)

                val r = randomOptions[left]

                if (r != null) {
                    for (right in r.keys){
                        val elem = r[right]
                        Timber.d("[$right]: ${elem?.correlationValue}, ${elem?.hasGroupInCommon}")
                    }
                }
            }
        }


        // todo use multipliers and randomness
        val updatedViewables = reviewCards.map {note ->
            val ll = randomOptions[note.id]!!.toList()
            val wrongs = ll.sortedByDescending { it.second.correlationValue + (if (it.second.hasGroupInCommon) 1.0 else 0.0) }.take(3).map { it.first }
            val wrongNotes = wrongs.map { it1 -> allCards.find { it1 == it.id }!! }
            val options = (wrongNotes+ listOf<AtomicNote>(note)).shuffled()

            val streakSoFar = reviews.find { it.question == note.id }!!.streak

            val viewableCard = PreparedViewableCard(options.map { it.answer }.indexOf(note.answer)+1, options.map { it.answer }, streakSoFar, note, options)

            return@map viewableCard

        }

        return updatedViewables+newViewables

    }

    private fun getNewViewables(newCount: Int, newCards:List<AtomicNote>): List<PreparedViewableCard> {

        Timber.d("Limiting to $newCount new cards")

        val newViewables = newCards.map{note ->
            val viewableCard = PreparedViewableCard(1, listOf(note.answer), -1, note, listOf(note))

            return@map viewableCard
        }.shuffled().take(newCount)

        return newViewables
    }

    // Given a review that is known to be most recent
    // Calculate the earliest level the card will appear on again
    private fun recentReviewToLevel(review: Review, deckMultiplier:Double):Int{
        return review.level+deckMultiplier.pow(review.streak).toInt()
    }

    private fun getDeckLevel(deckName: String, successMultiplier: Double):Int{
        val reviews = repository.getMostRecentReviewsByDeckName(deckName = deckName)

        val reviewDates = reviews.map {
            it.level+successMultiplier.pow(it.streak).toInt()
        }

        if (reviewDates.isEmpty()){
            return 1;
        }

        return reviewDates.min()

    }
}