package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class RemoveCardFromGroupUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<RemoveCardFromGroupUseCase.Input, Unit>() {
    override fun doStuff(input: RemoveCardFromGroupUseCase.Input) {
        repository.removeCardFromGroup(input.groupID, input.cardID)
    }

    data class Input(
        val groupID: String,
        val cardID: String
    )
}