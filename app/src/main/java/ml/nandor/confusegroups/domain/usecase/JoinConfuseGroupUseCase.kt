package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class JoinConfuseGroupUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<Pair<String, String>, Unit>() {
    override fun doStuff(input: Pair<String, String>) {
        Timber.d("${input.first} -- ${input.second}")
        repository.makeCardPartOfGroup(GroupMembership(Util.getCardName(), input.first, input.second))
    }
}