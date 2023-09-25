package ml.nandor.confusegroups.data

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
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

}