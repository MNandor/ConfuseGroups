package ml.nandor.confusegroups.domain.repository

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.Review

interface LocalStorageRepository {
    fun insertDeck(deck: Deck)
    fun listDecks():List<Deck>
    fun deleteDeckByName(deckName: String)

    fun insertCard(card: AtomicNote)

    fun getCardsByDeckName(deckName: String?):List<AtomicNote>

    fun getMostRecentReviewsByDeckName(deckName: String):List<Review>

    fun getDeckByName(deckName: String):Deck

    fun insertReview(review: Review)

    fun listReviews():List<Review>
}