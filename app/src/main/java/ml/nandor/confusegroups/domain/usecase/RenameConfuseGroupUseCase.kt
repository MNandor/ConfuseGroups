package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class RenameConfuseGroupUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Pair<String, String>, Unit>() {
    override fun doStuff(input: Pair<String, String>) {
        repository.renameConfuseGroup(input.first, input.second)
    }
}