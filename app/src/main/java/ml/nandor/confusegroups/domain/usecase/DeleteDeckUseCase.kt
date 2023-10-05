package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class DeleteDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, Unit>() {
    override fun doStuff(input: String) {
        repository.deleteDeckByName(input)
    }
}