package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class UpdateDeckPreferencesUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<UpdateDeckPreferencesUseCase.InputDC, Unit>() {
    override fun doStuff(input: InputDC) {

        // todo error check
        if (input.correlationPreference.isNotEmpty()){
            val num = input.correlationPreference.toDouble()
            repository.setDeckCorrelationPreferenceValue(input.deckName, num)
        }

        if (input.groupPreference.isNotEmpty()){
            val num = input.groupPreference.toDouble()
            repository.setDeckGroupPreferenceValue(input.deckName, num)
        }
        if (input.randomPreference.isNotEmpty()){
            val num = input.randomPreference.toDouble()
            repository.setDeckRandomPreferenceValue(input.deckName, num)
        }

        if (input.newCardsPerLevel.isNotEmpty()){
            val num = input.newCardsPerLevel.toInt()
            repository.setDeckNewCardsPerLevelValue(input.deckName, num)
        }

    }

    data class InputDC(
        val deckName: String,
        val correlationPreference: String,
        val groupPreference: String,
        val randomPreference: String,
        val newCardsPerLevel: String
    )
}