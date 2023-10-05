package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class GetDeckSizesUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Unit, List<DeckSize>>() {
    override fun doStuff(input: Unit):List<DeckSize> {
        return repository.getDeckSizes()
    }
}