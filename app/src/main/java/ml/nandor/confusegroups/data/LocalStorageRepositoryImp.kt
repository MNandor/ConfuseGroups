package ml.nandor.confusegroups.data

import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Inject

class LocalStorageRepositoryImp @Inject constructor(
    private val dao: DataAccessObject
): LocalStorageRepository {

}