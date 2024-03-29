package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class CreateConfuseGroupUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<ConfuseGroup, Unit>() {
    override fun doStuff(input: ConfuseGroup) {

        val newGroupID: String = Util.getCardName()


        repository.insertConfuseGroup(input)

    }
}