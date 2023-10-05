package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class InsertReviewUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Review, Unit>() {
    override fun doStuff(input: Review) {
        repository.insertReview(input)
    }
}