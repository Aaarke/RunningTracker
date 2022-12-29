package com.roshi.runningtrackerapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.roshi.runningtrackerapp.db.RunningDataBase
import com.roshi.runningtrackerapp.other.Constant.KEY_FIRST_TIME_TOGGLE
import com.roshi.runningtrackerapp.other.Constant.KEY_NAME
import com.roshi.runningtrackerapp.other.Constant.KEY_WEIGHT
import com.roshi.runningtrackerapp.other.Constant.RUNNING_DATABASE_NAME
import com.roshi.runningtrackerapp.other.Constant.SHARED_PREFERENCES_NAME
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
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        RunningDataBase::class.java, RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunningDao(dataBase: RunningDataBase) = dataBase.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences = app.getSharedPreferences(
        SHARED_PREFERENCES_NAME, MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 60f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true
    )


}