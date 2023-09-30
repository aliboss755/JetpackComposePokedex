package com.example.jetpackcomposepokedex.di

import com.example.jetpackcomposepokedex.data.remote.PokeApi
import com.example.jetpackcomposepokedex.repository.PokemonRepository
import com.example.jetpackcomposepokedex.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
 fun providePokemonRepository(
     api: PokeApi
 )=PokemonRepository(api)
    @Singleton
    @Provides
    fun providePokemonApi():PokeApi{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
            .create()
    }
}