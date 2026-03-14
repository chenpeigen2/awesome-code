package com.peter.dagger.demo.di

import android.app.Application
import android.content.Context
import com.peter.dagger.demo.model.CoffeeMaker
import com.peter.dagger.demo.model.ElectricHeater
import com.peter.dagger.demo.model.Heater
import com.peter.dagger.demo.model.Pump
import com.peter.dagger.demo.model.Thermosiphon
import com.peter.dagger.demo.qualifier.DataSource
import com.peter.dagger.demo.qualifier.LocalDataSourceImpl
import com.peter.dagger.demo.qualifier.RemoteDataSourceImpl
import com.peter.dagger.demo.scope.DatabaseService
import com.peter.dagger.demo.scope.RequestService
import com.peter.dagger.demo.scope.UserService
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * AppModule - 应用级 Dagger 模块
 */
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideHeater(): Heater {
        return ElectricHeater()
    }

    @Provides
    @Singleton
    fun providePump(heater: Heater): Pump {
        return Thermosiphon(heater)
    }

    @Provides
    @Singleton
    fun provideCoffeeMaker(heater: Heater, pump: Pump): CoffeeMaker {
        return CoffeeMaker(heater, pump)
    }

    @Provides
    @Singleton
    @Named("local")
    fun provideLocalDataSource(): DataSource {
        return LocalDataSourceImpl()
    }

    @Provides
    @Singleton
    @Named("remote")
    fun provideRemoteDataSource(): DataSource {
        return RemoteDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideDatabaseService(): DatabaseService {
        return DatabaseService()
    }

    @Provides
    fun provideRequestService(): RequestService {
        return RequestService()
    }

    @Provides
    fun provideUserService(): UserService {
        return UserService()
    }
}