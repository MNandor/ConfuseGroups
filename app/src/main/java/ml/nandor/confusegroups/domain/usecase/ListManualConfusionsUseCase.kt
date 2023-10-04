package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class ListManualConfusionsUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Unit, List<ManualConfusion>>() {
    override fun doStuff(input: Unit):List<ManualConfusion> {
        return repository.listManualConfusions()
    }
}