package com.zarholding.zar.database

import android.content.Context
import androidx.room.Room
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {


    //---------------------------------------------------------------------------------------------- providerArticleDao
    @Provides
    @Singleton
    fun providerArticleDao(appDatabase: AppDatabase) : ArticleDao {
        return appDatabase.articleDao()
    }
    //---------------------------------------------------------------------------------------------- providerArticleDao



    //---------------------------------------------------------------------------------------------- providerUserInfoDao
    @Provides
    @Singleton
    fun providerUserInfoDao(appDatabase: AppDatabase) : UserInfoDao {
        return appDatabase.userInfoDao()
    }
    //---------------------------------------------------------------------------------------------- providerUserInfoDao



    //---------------------------------------------------------------------------------------------- providerRoleDao
    @Provides
    @Singleton
    fun providerRoleDao(appDatabase: AppDatabase) : RoleDao {
        return appDatabase.roleDao()
    }
    //---------------------------------------------------------------------------------------------- providerRoleDao




    //---------------------------------------------------------------------------------------------- provideAppDatabase
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "zarholding"
        ).allowMainThreadQueries().build()
    }
    //---------------------------------------------------------------------------------------------- provideAppDatabase

}