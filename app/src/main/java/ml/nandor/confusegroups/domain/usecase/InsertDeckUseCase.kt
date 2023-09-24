package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class InsertDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Deck, Unit>() {
    override fun doStuff(input: Deck) {
        repository.insertDeck(input)
    }
}