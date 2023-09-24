package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class ListCardsFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String?, List<AtomicNote>>() {
    override fun doStuff(input: String?): List<AtomicNote> {
        return if (input == null)
            listOf()
        else
            repository.getCardsByDeckName(input)
    }
}