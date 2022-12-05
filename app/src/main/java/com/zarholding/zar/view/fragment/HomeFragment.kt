package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.zar.core.tools.autoimageslider.SliderAnimations
import com.zar.core.tools.autoimageslider.SliderView
import com.zar.core.tools.manager.DialogManager
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.dialog.ArticleDetailDialog
import com.zarholding.zar.view.recycler.adapter.AppAdapter
import com.zarholding.zar.view.recycler.adapter.BannerAdapter
import com.zarholding.zar.view.recycler.adapter.NewsAdapter
import com.zarholding.zar.view.recycler.adapter.RequestAdapter
import com.zarholding.zar.view.recycler.holder.AppItemHolder
import com.zarholding.zar.view.recycler.holder.NewsItemHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zar.R
import zar.databinding.FragmentHomeBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/13/2022.
 */

@AndroidEntryPoint
class HomeFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var articleDao: ArticleDao


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initApps()
        getBannerFromDB()
        initNews()
        initPersonnelRequest()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 10 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- initApps
    private fun initApps() {
        val apps: MutableList<AppModel> = mutableListOf()

        apps.add(
            AppModel(
                R.drawable.icon_trip,
                getString(R.string.tripAndMap),
                R.id.action_HomeFragment_to_ServiceFragment
            )
        )

        apps.add(AppModel(R.drawable.icon_personnel, getString(R.string.personnelList), 0))
        apps.add(AppModel(R.drawable.icon_food_reservation, getString(R.string.foodReservation), 0))
        apps.add(AppModel(R.drawable.icon_gift_card, getString(R.string.giftCard), 0))
        setAppsAdapter(apps)
    }
    //---------------------------------------------------------------------------------------------- initApps


    //---------------------------------------------------------------------------------------------- setAppsAdapter
    private fun setAppsAdapter(apps: MutableList<AppModel>) {
        val click = object : AppItemHolder.Click {
            override fun appClick(action: Int) {
                if (action != 0)
                    findNavController().navigate(action)
            }
        }
        val adapter = AppAdapter(apps, click)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        linearLayoutManager.reverseLayout = true
        binding.recyclerViewApps.layoutManager = linearLayoutManager
        binding.recyclerViewApps.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setAppsAdapter


    //---------------------------------------------------------------------------------------------- getBannerFromDB
    private fun getBannerFromDB() {
        CoroutineScope(IO).launch {
            val banners = articleDao.getArticles(EnumArticleType.SlideShow.name)
            withContext(Main) {
                setBannerSlider(banners)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- getBannerFromDB


    //---------------------------------------------------------------------------------------------- setBannerSlider
    private fun setBannerSlider(banners: List<ArticleEntity>) {
        val adapter = BannerAdapter(banners)
        binding.sliderViewBanner.setSliderAdapter(adapter)
        binding.sliderViewBanner.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderViewBanner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderViewBanner.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
    }
    //---------------------------------------------------------------------------------------------- setBannerSlider


    //---------------------------------------------------------------------------------------------- initNews
    private fun initNews() {
        CoroutineScope(IO).launch {
            val news = articleDao.getArticles(EnumArticleType.Article.name)
            withContext(Main) {
                setNewsAdapter(news)
            }
        }

    }
    //---------------------------------------------------------------------------------------------- initNews


    //---------------------------------------------------------------------------------------------- setNewsAdapter
    private fun setNewsAdapter(news: List<ArticleEntity>) {
        val click = object : NewsItemHolder.Click {
            override fun detailArticle(article: ArticleEntity) {
                ArticleDetailDialog(requireContext(), article).show()
            }
        }
        val adapter = NewsAdapter(news, click)

        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        linearLayoutManager.reverseLayout = true

        binding.recyclerViewNews.layoutManager = linearLayoutManager
        binding.recyclerViewNews.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setNewsAdapter





    //---------------------------------------------------------------------------------------------- initPersonnelRequest
    private fun initPersonnelRequest() {
        val apps: MutableList<AppModel> = mutableListOf()
        apps.add(AppModel(R.drawable.ic_requests, getString(R.string.personnelRequest), 0))
        apps.add(AppModel(R.drawable.ic_requests, getString(R.string.personnelRequest), 0))
        apps.add(AppModel(R.drawable.ic_requests, getString(R.string.personnelRequest), 0))
        apps.add(AppModel(R.drawable.ic_requests, getString(R.string.personnelRequest), 0))
        setPersonnelRequestAdapter(apps)
    }
    //---------------------------------------------------------------------------------------------- initPersonnelRequest


    //---------------------------------------------------------------------------------------------- setPersonnelRequestAdapter
    private fun setPersonnelRequestAdapter(apps: MutableList<AppModel>) {
        val adapter = RequestAdapter(apps)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        linearLayoutManager.reverseLayout = true

        binding.recyclerViewRequest.layoutManager = linearLayoutManager
        binding.recyclerViewRequest.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setPersonnelRequestAdapter


/*    private fun setSliderAdapter() {
        val apps: MutableList<AppModel> = mutableListOf()
        apps.add(AppModel(R.drawable.icon_trip, getString(R.string.tripAndMap), 1))
        apps.add(AppModel(R.drawable.icon_personnel, getString(R.string.personnelList), 1))
        apps.add(AppModel(R.drawable.icon_food_reservation, getString(R.string.foodReservation), 0))
        apps.add(AppModel(R.drawable.icon_gift_card, getString(R.string.giftCard), 0))
        binding.viewPager.adapter = SliderAdapter(apps)
    }*/


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}