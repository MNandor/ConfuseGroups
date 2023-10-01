package ml.nandor.confusegroups.data

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class LocalStorageRepositoryImp @Inject constructor(
    private val dao: DataAccessObject
): LocalStorageRepository {
    override fun insertDeck(deck: Deck) {
        dao.insertDeck(deck)
    }

    override fun listDecks(): List<Deck> {
        return dao.listDecks()
    }

    override fun deleteDeckByName(deckName: String) {
        dao.deleteDeckByName(deckName)
    }

    override fun insertCard(card: AtomicNote) {
        Timber.d("Inserting note ${card.question} - ${card.answer}")
        dao.insertCard(card)
    }

    override fun getCardsByDeckName(deckName: String?):List<AtomicNote> {
        return if (deckName == null){
            listOf()
        } else {
            dao.getCardsByDeckName(deckName)
        }

    }

    override fun getMostRecentReviewsByDeckName(deckName: String): List<Review> {
        return dao.getMostRecentReviewsByDeckName(deckName)
    }

    override fun getDeckByName(deckName: String): Deck {
        return dao.getDeckByName(deckName)
    }

    override fun insertReview(review: Review){
        dao.insertReview(review)
        Timber.d("Storing review ${review.question} - ${review.answer} with streak ${review.streak}")
        Timber.d("Unpicked answers are: ${review.unpickedAnswers}")
    }

    override fun listReviews(): List<Review> {
        return dao.listReviews()
    }

    override fun getNotesMatchingAnswers(answer: String): List<AtomicNote> {
        return dao.getNotesMatchingAnswer(answer)
    }

    override fun getDeckSizes(): List<DeckSize> {
        return dao.getDeckSizes()
    }

}