package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.model.other.AppModel
import com.zarholding.zar.model.response.banner.BannerModel
import com.zarholding.zar.model.response.news.NewsModel
import com.zarholding.zar.model.response.user.UserInfoModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.view.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.zarholding.zar.view.autoimageslider.SliderAnimations
import com.zarholding.zar.view.autoimageslider.SliderView
import com.zarholding.zar.view.recycler.adapter.AppAdapter
import com.zarholding.zar.view.recycler.adapter.BannerAdapter
import com.zarholding.zar.view.recycler.adapter.NewsAdapter
import com.zarholding.zar.view.recycler.adapter.RequestAdapter
import com.zarholding.zar.view.recycler.holder.AppItemHolder
import zar.R
import zar.databinding.FragmentHomeBinding

/**
 * Created by m-latifi on 11/13/2022.
 */

class HomeFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var userModel : UserInfoModel? = null


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
        userModel = arguments?.getParcelable(CompanionValues.USER_INFO)
        initApps()
        initBanner()
        initNews()
        initPersonnelRequest()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- initApps
    private fun initApps() {
        val apps: MutableList<AppModel> = mutableListOf()

        apps.add(AppModel(
            R.drawable.icon_trip,
            getString(R.string.tripAndMap),
            R.id.action_HomeFragment_to_ServiceFragment))

        apps.add(AppModel(R.drawable.icon_personnel, getString(R.string.personnelList), 1))
        apps.add(AppModel(R.drawable.icon_food_reservation, getString(R.string.foodReservation), 0))
        apps.add(AppModel(R.drawable.icon_gift_card, getString(R.string.giftCard), 0))
        setAppsAdapter(apps)
    }
    //---------------------------------------------------------------------------------------------- initApps



    //---------------------------------------------------------------------------------------------- setAppsAdapter
    private fun setAppsAdapter(apps: MutableList<AppModel>) {
        val click = object : AppItemHolder.Click {
            override fun appClick(action: Int) {
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



    //---------------------------------------------------------------------------------------------- initBanner
    private fun initBanner() {
        val banners: MutableList<BannerModel> = mutableListOf()
        banners.add(BannerModel("https://irp.cdn-website.com/29726fa2/dms3rep/multi/Double-width-6m-long-1920w+%281%29.jpg"))
        banners.add(BannerModel("https://www.bradleysmoker.com/wp-content/uploads/2010/11/Smoked-Sliders-scaled.jpeg"))
        setBannerSlider(banners)
    }
    //---------------------------------------------------------------------------------------------- initBanner



    //---------------------------------------------------------------------------------------------- setBannerSlider
    private fun setBannerSlider(banners: MutableList<BannerModel>) {
        val adapter = BannerAdapter(banners)
        binding.sliderViewBanner.setSliderAdapter(adapter)
        binding.sliderViewBanner.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderViewBanner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderViewBanner.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
    }
    //---------------------------------------------------------------------------------------------- setBannerSlider



    //---------------------------------------------------------------------------------------------- initNews
    private fun initNews() {
        val news: MutableList<NewsModel> = mutableListOf()
        news.add(NewsModel("توزیع سهمیه شهریور","برگزاری کلاسهای مدیریت دانش در سالن آمفی تئاتر زر ماکارون", "https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png"))
        news.add(NewsModel("توزیع سهمیه شهریور","برگزاری کلاسهای مدیریت دانش در سالن آمفی تئاتر زر ماکارون", "https://fs.noorgram.ir/xen/2022/02/3098_5479a701607915b9791e6c23f3e07cb6.png"))
        news.add(NewsModel("توزیع سهمیه شهریور","برگزاری کلاسهای مدیریت دانش در سالن آمفی تئاتر زر ماکارون", "https://azadimarket.com/wp-content/uploads/2021/09/19cd3daa135c4f66f5f5df0f1889f527.png"))
        setNewsAdapter(news)
    }
    //---------------------------------------------------------------------------------------------- initNews



    //---------------------------------------------------------------------------------------------- setNewsAdapter
    private fun setNewsAdapter(news: MutableList<NewsModel>) {
        val adapter = NewsAdapter(news)

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