package com.zarholding.zar.viewmodel

import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.hilt.ResourcesProvider
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import zar.R
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val resourcesProvider: ResourcesProvider
) : ZarViewModel() {


    //---------------------------------------------------------------------------------------------- getApp
    fun getApp(): MutableList<AppModel> {
        val apps: MutableList<AppModel> = mutableListOf()
        apps.add(
            AppModel(
                R.drawable.icon_trip,
                resourcesProvider.getString(R.string.tripAndMap),
                R.id.action_HomeFragment_to_BusServiceFragment
            )
        )
        apps.add(
            AppModel(
                R.drawable.ic_taxi,
                resourcesProvider.getString(R.string.taxiReservation),
                R.id.action_HomeFragment_to_TaxiReservationFragment
            )
        )
        apps.add(
            AppModel(
                R.drawable.ic_parking,
                resourcesProvider.getString(R.string.parking),
                R.id.action_HomeFragment_to_ParkingFragment
            )
        )
        apps.add(
            AppModel(
                R.drawable.icon_personnel,
                resourcesProvider.getString(R.string.personnelList),
                0
            )
        )
        apps.add(
            AppModel(
                R.drawable.icon_food_reservation,
                resourcesProvider.getString(R.string.foodReservation),
                0
            )
        )

        return apps
    }
    //---------------------------------------------------------------------------------------------- getApp


    //---------------------------------------------------------------------------------------------- getPersonnelRequest
    fun getPersonnelRequest(): MutableList<AppModel> {
        val apps: MutableList<AppModel> = mutableListOf()
        apps.add(
            AppModel(
                R.drawable.ic_requests,
                resourcesProvider.getString(R.string.personnelRequest),
                0
            )
        )
        apps.add(
            AppModel(
                R.drawable.ic_requests,
                resourcesProvider.getString(R.string.personnelRequest),
                0
            )
        )
        apps.add(
            AppModel(
                R.drawable.ic_requests,
                resourcesProvider.getString(R.string.personnelRequest),
                0
            )
        )
        apps.add(
            AppModel(
                R.drawable.ic_requests,
                resourcesProvider.getString(R.string.personnelRequest),
                0
            )
        )
        return apps
    }
    //---------------------------------------------------------------------------------------------- getPersonnelRequest


    //---------------------------------------------------------------------------------------------- getArticles
    fun getArticles(type: EnumArticleType): List<ArticleEntity> {
        return articleRepository.getArticles(type)
    }
    //---------------------------------------------------------------------------------------------- getArticles

}