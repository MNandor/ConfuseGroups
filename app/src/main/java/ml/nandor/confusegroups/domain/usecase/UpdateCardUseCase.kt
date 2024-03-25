package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<AtomicNote, Unit>() {
    override fun doStuff(input: AtomicNote) {
        repository.updateCard(input)
    }
}