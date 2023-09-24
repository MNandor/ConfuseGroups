package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class GetViewablesFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String?, List<PreparedViewableCard>>() {
    override fun doStuff(deckName: String?): List<PreparedViewableCard> {

        val allCards = repository.getCardsByDeckName(deckName)
        val viewAbles = allCards.map {note ->
            val possiblesWrongs = allCards
                .filter { it.question != note.question && it.answer != note.answer }
                .map { it -> it.answer }
                .toMutableList()
            // todo error out instead
            while (possiblesWrongs.size < 3)
                possiblesWrongs.add("Placeholder")

            val answers = possiblesWrongs.shuffled().take(3).toMutableList()

            answers.add(note.answer)

            val options = answers.shuffled()

            val viewableCard = PreparedViewableCard(note.question, options.indexOf(note.answer)+1, options)

            return@map viewableCard

        }

        return viewAbles

    }
}