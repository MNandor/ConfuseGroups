package ml.nandor.confusegroups.data

import ml.nandor.confusegroups.domain.model.Deck
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

}