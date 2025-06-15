package ru.netology.nmedia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.auth.AuthRepository
import ru.netology.nmedia.auth.AuthRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    @Singleton
    fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
