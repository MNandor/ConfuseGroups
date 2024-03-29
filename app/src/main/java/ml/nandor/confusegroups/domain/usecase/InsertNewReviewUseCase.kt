package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class InsertNewReviewUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<NewReview, Unit>() {
    override fun doStuff(input: NewReview) {
        repository.insertNewReview(input)
    }
}