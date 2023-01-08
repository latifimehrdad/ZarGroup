package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zar.core.tools.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.zar.core.tools.autoimageslider.SliderAnimations
import com.zar.core.tools.autoimageslider.SliderView
import com.zarholding.zar.database.entity.ArticleEntity
import com.zarholding.zar.model.enum.EnumArticleType
import com.zarholding.zar.view.dialog.ArticleDetailDialog
import com.zarholding.zar.view.dialog.ConfirmDialog
import com.zarholding.zar.view.recycler.adapter.AppAdapter
import com.zarholding.zar.view.recycler.adapter.BannerAdapter
import com.zarholding.zar.view.recycler.adapter.NewsAdapter
import com.zarholding.zar.view.recycler.adapter.RequestAdapter
import com.zarholding.zar.view.recycler.holder.AppItemHolder
import com.zarholding.zar.view.recycler.holder.NewsItemHolder
import com.zarholding.zar.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.FragmentHomeBinding

/**
 * Created by m-latifi on 11/13/2022.
 */

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        backClickControl()
        setAppsAdapter()
        getSlides()
        getNews()
        setPersonnelRequestAdapter()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- backClickControl
    private fun backClickControl() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    context?.let {
                        ConfirmDialog(
                            it,
                            ConfirmDialog.ConfirmType.DELETE,
                            getString(R.string.doYouWantToExitApp),
                            object : ConfirmDialog.Click {
                                override fun clickYes() {
                                    activity?.finish()
                                }
                            }).show()
                    }
                }
            })
    }
    //---------------------------------------------------------------------------------------------- backClickControl


    //---------------------------------------------------------------------------------------------- setAppsAdapter
    private fun setAppsAdapter() {
        val click = object : AppItemHolder.Click {
            override fun appClick(action: Int) {
                if (action != 0)
                    findNavController().navigate(action)
            }
        }
        val adapter = AppAdapter(homeViewModel.getApp(), click)
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


    //---------------------------------------------------------------------------------------------- getSlides
    private fun getSlides() {
        val slides = homeViewModel.getArticles(EnumArticleType.SlideShow)
        setBannerSlider(slides)
    }
    //---------------------------------------------------------------------------------------------- getSlides


    //---------------------------------------------------------------------------------------------- setBannerSlider
    private fun setBannerSlider(slides: List<ArticleEntity>) {
        val adapter = BannerAdapter(slides)
        binding.sliderViewBanner.setSliderAdapter(adapter)
        binding.sliderViewBanner.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderViewBanner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderViewBanner.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.sliderViewBanner.startAutoCycle()
    }
    //---------------------------------------------------------------------------------------------- setBannerSlider


    //---------------------------------------------------------------------------------------------- getNews
    private fun getNews() {
        val news = homeViewModel.getArticles(EnumArticleType.Article)
        setNewsAdapter(news)
    }
    //---------------------------------------------------------------------------------------------- getNews


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


    //---------------------------------------------------------------------------------------------- setPersonnelRequestAdapter
    private fun setPersonnelRequestAdapter() {
        val adapter = RequestAdapter(homeViewModel.getPersonnelRequest())
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


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}