package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class RenameDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Pair<String, String>, Unit>() {
    override fun doStuff(input: Pair<String, String>) {
        repository.renameDeck(input.first, input.second)
    }
}