package com.zarholding.zar.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.view.fragment.ServiceFragment
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.DialogPersonnelBinding
import javax.inject.Inject

@AndroidEntryPoint
class PersonnelDialog(
    private val click: Click
) : DialogFragment(){

    lateinit var binding : DialogPersonnelBinding

    private val loadingManager = LoadingManager()
    private val userViewModel : UserViewModel by viewModels()
    private val tokenViewModel : TokenViewModel by viewModels()

    @Inject
    lateinit var userInfoDao: UserInfoDao

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes()
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPersonnelBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lp = WindowManager.LayoutParams()
        val window = dialog!!.window
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        window!!.setBackgroundDrawable(inset)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        binding.materialButtonSelect.setOnClickListener {
            requestGetUser()
        }

    }
    //---------------------------------------------------------------------------------------------- onCreateView



    //---------------------------------------------------------------------------------------------- requestGetUser
    private fun requestGetUser() {
        startLoading()
        userViewModel.requestGetUser("fullName,userName", "مهرداد", tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingRecycler()
                response?.let {
                    if (!it.hasError) {
                        it.data?.let {

                        }
                    }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetUser




    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewPersonnel,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


}