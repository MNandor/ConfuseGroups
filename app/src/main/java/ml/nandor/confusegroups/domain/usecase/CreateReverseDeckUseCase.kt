package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class CreateReverseDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, Unit>() {
    override fun doStuff(input: String) {
        val cards = repository.getCardsByDeckName(input)
        val deck = repository.getDeckByName(input)

        val newDeck = Deck(Util.getDeckName(), deck.newCardsPerLevel, deck.successMultiplier, deck.confuseExponent, deck.failExponent, "<"+deck.displayName+">")

        repository.insertDeck(newDeck)

        Timber.d("Creating $newDeck")

        for (card in cards){
            val newCard = AtomicNote(Util.getCardName(), card.question?:"", newDeck.name, card.answer, card.mnemonic)
            repository.insertCard(newCard)
            Timber.d("Creating $newCard")
        }

        // todo transaction
    }
}