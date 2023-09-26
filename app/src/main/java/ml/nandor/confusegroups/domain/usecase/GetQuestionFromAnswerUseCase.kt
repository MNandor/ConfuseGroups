package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject
import kotlin.math.pow

class GetQuestionFromAnswerUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, String>() {
    override fun doStuff(input: String): String {
        val notes = repository.getNotesMatchingAnswers(input)

        return notes.first().question
    }
}