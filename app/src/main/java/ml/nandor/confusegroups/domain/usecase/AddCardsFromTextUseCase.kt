package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.TextImport
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

// todo usecase has no error checking whatsoever
class AddCardsFromTextUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Pair<String, String>, Unit>() {
    override fun doStuff(input: Pair<String, String>) {

        val deck = input.first
        val data = input.second

        for (pair in data.split(";", "\n")){

            if (pair.startsWith("#")) continue // corresponds to exported group headers

            val (question, answer) = TextImport.textToQAPair(pair) ?: return

            val card = AtomicNote(Util.getCardName(), answer = answer, deck = deck, question)
            repository.insertCard(card)

        }

    }
}