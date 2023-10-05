package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class GetQuestionFromAnswerUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String, String>() {
    override fun doStuff(input: String): String {
        val notes = repository.getNotesMatchingAnswers(input)

        return notes.first().question
    }
}