package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.SettingKV
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class SetKeyValueUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<SettingKV, Unit>() {
    override fun doStuff(input: SettingKV) {
        repository.setKV(input)
    }
}