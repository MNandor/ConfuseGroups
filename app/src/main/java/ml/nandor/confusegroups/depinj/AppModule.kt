package ml.nandor.confusegroups.depinj

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ml.nandor.confusegroups.data.LocalStorageDatabase
import ml.nandor.confusegroups.data.LocalStorageRepositoryImp
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalStorageRepository(database: LocalStorageDatabase): LocalStorageRepository{
        return LocalStorageRepositoryImp(database.dao())
    }

    @Provides
    fun providesDatabase(@ApplicationContext appContext: Context): LocalStorageDatabase {
        return LocalStorageDatabase.getDatabase(appContext)
    }
}