package com.zarholding.zar.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.view.recycler.adapter.PersonnelSelectAdapter
import com.zarholding.zar.view.recycler.holder.PersonnelSelectHolder
import com.zarholding.zar.viewmodel.TokenViewModel
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.DialogPersonnelBinding
import javax.inject.Inject

@AndroidEntryPoint
class PersonnelDialog(
    private val chooseItem: Click
) : DialogFragment(){

    lateinit var binding : DialogPersonnelBinding

    private val loadingManager = LoadingManager()
    private val userViewModel : UserViewModel by viewModels()
    private val tokenViewModel : TokenViewModel by viewModels()

    private var job : Job? = null

    @Inject
    lateinit var userInfoDao: UserInfoDao

    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun select(item: UserInfoEntity)
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
        setListener()
    }
    //---------------------------------------------------------------------------------------------- onCreateView



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.textInputEditTextSearch.addTextChangedListener {
            job?.cancel()
            createJobForSearch(it.toString())
        }

    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- createJobForSearch
    private fun createJobForSearch(search : String) {
        job = CoroutineScope(IO).launch {
            delay(800)
            withContext(Main) {
                requestGetUser(search)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- createJobForSearch



    //---------------------------------------------------------------------------------------------- requestGetUser
    private fun requestGetUser(search : String) {
        startLoading()
        userViewModel.requestGetUser("fullName,userName", search, tokenViewModel.getBearerToken())
            .observe(viewLifecycleOwner) { response ->
                loadingManager.stopLoadingRecycler()
                response?.let {data ->
                    if (!data.hasError) {
                        data.data?.let {item ->
                            item.items?.let {
                                setPersonnelAdapter(it)
                            }
                        }
                    }
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetUser



    //---------------------------------------------------------------------------------------------- setPersonnelAdapter
    private fun setPersonnelAdapter(items : List<UserInfoEntity>) {
        val select = object : PersonnelSelectHolder.Click {
            override fun select(item: UserInfoEntity) {
                chooseItem.select(item)
                dismiss()
            }
        }

        val adapter = PersonnelSelectAdapter(items,select)
        val manager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerViewPersonnel.layoutManager = manager
        binding.recyclerViewPersonnel.adapter = adapter
    }
    //---------------------------------------------------------------------------------------------- setPersonnelAdapter


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewPersonnel,
            R.layout.item_loading_light,
            R.color.recyclerLoadingShadow,
            1
        )
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- dismiss
    override fun dismiss() {
        super.dismiss()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- dismiss
}