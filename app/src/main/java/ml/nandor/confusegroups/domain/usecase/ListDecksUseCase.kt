package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class ListDecksUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Unit, List<Deck>>() {
    override fun doStuff(input: Unit):List<Deck> {
        return repository.listDecks()
    }
}