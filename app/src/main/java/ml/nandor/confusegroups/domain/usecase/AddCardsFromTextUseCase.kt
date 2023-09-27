package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

// todo usecase has no error checking whatsoever
class AddCardsFromTextUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Pair<String, String>, Unit>() {
    override fun doStuff(input: Pair<String, String>) {

        val deck = input.first
        val data = input.second

        for (pair in data.split(";")){
            val qa = pair.split("-")

            val card = AtomicNote(question = qa[0].trim(), answer = qa[1].trim(), deck = deck)
            repository.insertCard(card)

        }

    }
}