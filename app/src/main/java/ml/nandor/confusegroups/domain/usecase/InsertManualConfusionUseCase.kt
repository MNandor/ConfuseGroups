package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class InsertManualConfusionUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<ManualConfusion, Unit>() {
    override fun doStuff(input: ManualConfusion) {

        if (input.leftCard < input.rightCard)
            repository.insertManualConfusion(input)
        else if (input.leftCard > input.rightCard)
            repository.insertManualConfusion(ManualConfusion(leftCard = input.rightCard, rightCard = input.leftCard))
    }
}