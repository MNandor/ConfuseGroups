package ml.nandor.confusegroups.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import java.lang.Exception
import javax.inject.Inject

// let's put all the boilerplate in one class
abstract class UseCase<Input, Output> {
    operator fun invoke(input: Input): Flow<Resource<Output>> = flow {
        emit (Resource.Loading())
        val result = try {
            val result = doStuff(input)
            Resource.Success(result)
        } catch (e: Exception){
            Resource.Error(e.localizedMessage?: "Unknown error")
        }
        emit(result)
    }

    protected abstract fun doStuff(input: Input): Output
}