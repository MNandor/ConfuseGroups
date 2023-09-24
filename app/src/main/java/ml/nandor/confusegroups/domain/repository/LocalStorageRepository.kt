package ml.nandor.confusegroups.domain.repository

import ml.nandor.confusegroups.domain.model.Deck

interface LocalStorageRepository {
    fun insertDeck(deck: Deck)

    fun listDecks():List<Deck>
}