package ml.nandor.confusegroups.domain.repository

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck

interface LocalStorageRepository {
    fun insertDeck(deck: Deck)
    fun listDecks():List<Deck>
    fun deleteDeckByName(deckName: String)

    fun insertCard(card: AtomicNote)
}