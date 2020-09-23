package com.roshi.runningtrackerapp.di

import android.content.Context
import androidx.room.Room
import com.roshi.runningtrackerapp.db.RunningDataBase
import com.roshi.runningtrackerapp.other.Constant.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app:Context)=Room.databaseBuilder(app,
        RunningDataBase::class.java,RUNNING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunningDao(dataBase: RunningDataBase)=dataBase.getRunDao()
}