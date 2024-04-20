package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.SettingKV
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class GetKeyValueUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, SettingKV>() {
    override fun doStuff(input: String): SettingKV {
        return repository.getKV(input)
    }
}