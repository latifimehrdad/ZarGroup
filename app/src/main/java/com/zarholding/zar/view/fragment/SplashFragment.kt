package com.zarholding.zar.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zar.database.dao.ArticleDao
import com.zarholding.zar.database.dao.RoleDao
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.RoleEntity
import com.zarholding.zar.model.request.ArticleRequestModel
import com.zarholding.zar.repository.UserRepository
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.ArticleViewModel
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.FragmentSplashBinding
import javax.inject.Inject

/**
 * Created by m-latifi on 11/8/2022.
 */


@AndroidEntryPoint
class SplashFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var articleDao: ArticleDao

    @Inject
    lateinit var userInfoDao: UserInfoDao

    @Inject
    lateinit var roleDao: RoleDao

    private lateinit var job: Job
    private val tokenViewModel: TokenViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val articleViewModel: ArticleViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        checkUserIsLogged()
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


    //---------------------------------------------------------------------------------------------- checkUserIsLogged
    private fun checkUserIsLogged() {
        val token = sharedPreferences.getString(CompanionValues.TOKEN, null)
        token?.let {
            gotoFragmentHome()
        } ?: gotoFragmentLogin()
    }
    //---------------------------------------------------------------------------------------------- checkUserIsLogged


    //---------------------------------------------------------------------------------------------- gotoFragmentLogin
    private fun gotoFragmentLogin() {
        job = CoroutineScope(IO).launch {
            delay(5000)
            withContext(Main) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentLogin


    //---------------------------------------------------------------------------------------------- gotoFragmentHome
    private fun gotoFragmentHome() {
        job = CoroutineScope(IO).launch {
            delay(1000)
            withContext(Main) {
                requestUserInfo()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- gotoFragmentHome


    //---------------------------------------------------------------------------------------------- requestUserInfo
    private fun requestUserInfo() {
        userViewModel.requestUserInfo(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let { userInfo ->
                    if (userInfo.hasError)
                        onError(EnumErrorType.UNKNOWN, userInfo.message)
                    else
                        userInfo.data?.let {
                            CoroutineScope(IO).launch {
                                userInfoDao.insertUserInfo(it)
                                withContext(Main) {
                                    (activity as MainActivity).setUserInfo()
                                    requestUserPermission()
                                }
                            }
                        } ?: run {
                            onError(
                                EnumErrorType.UNKNOWN,
                                resources.getString(R.string.responseUserInfoIsEmpty)
                            )
                        }
                } ?: run {
                    onError(
                        EnumErrorType.UNKNOWN,
                        resources.getString(R.string.responseUserInfoIsEmpty)
                    )
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestUserInfo



    //---------------------------------------------------------------------------------------------- requestUserPermission
    private fun requestUserPermission() {
        userViewModel.requestUserPermission(tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let { permissions ->
                    if (permissions.hasError)
                        onError(EnumErrorType.UNKNOWN, permissions.message)
                    else
                        permissions.data?.let { list ->
                            val temp : List<RoleEntity> = list.map { RoleEntity(it) }
                            CoroutineScope(IO).launch {
                                roleDao.deleteAllRecord()
                                roleDao.insert(temp)
                                withContext(Main) {
                                    requestGetSlideShow()
                                }
                            }
                        } ?: run {
                            requestGetSlideShow()
                        }
                } ?: run {
                    requestGetSlideShow()
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestUserPermission



    //---------------------------------------------------------------------------------------------- requestGetSlideShow
    private fun requestGetSlideShow() {
        val request = ArticleRequestModel(
            1,
            100,
            "",
            false,
            "SlideShow"
        )
        articleViewModel.requestGetArticles(request, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let { articleModels ->
                    if (articleModels.hasError)
                        onError(EnumErrorType.UNKNOWN, articleModels.message)
                    else
                        articleModels.data?.let { data ->
                            CoroutineScope(IO).launch {
                                for (item in data.items)
                                    item?.let { articleDao.insertArticle(it) }
                                withContext(Main) {
                                    requestGetArticle()
                                }
                            }
                        } ?: run {
                            onError(
                                EnumErrorType.UNKNOWN,
                                resources.getString(R.string.responseSlideIsEmpty)
                            )
                        }
                } ?: run {
                    onError(
                        EnumErrorType.UNKNOWN,
                        resources.getString(R.string.responseSlideIsEmpty)
                    )
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetSlideShow


    //---------------------------------------------------------------------------------------------- requestGetArticle
    private fun requestGetArticle() {
        val request = ArticleRequestModel(
            1,
            100,
            "",
            false,
            "Article"
        )
        articleViewModel.requestGetArticles(request, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                response?.let { articleModels ->
                    if (articleModels.hasError)
                        onError(EnumErrorType.UNKNOWN, articleModels.message)
                    else
                        articleModels.data?.let { data ->
                            CoroutineScope(IO).launch {
                                for (item in data.items)
                                    item?.let { articleDao.insertArticle(it) }
                                withContext(Main) {
                                    findNavController()
                                        .navigate(R.id.action_splashFragment_to_HomeFragment)
                                }
                            }
                        } ?: run {
                            onError(
                                EnumErrorType.UNKNOWN,
                                resources.getString(R.string.responseArticleIsEmpty)
                            )
                        }
                } ?: run {
                    onError(
                        EnumErrorType.UNKNOWN,
                        resources.getString(R.string.responseArticleIsEmpty)
                    )
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetArticle


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}