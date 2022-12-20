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
import com.zarholding.zar.database.entity.UserInfoEntity
import com.zarholding.zar.utility.EndlessScrollListener
import com.zarholding.zar.view.recycler.adapter.PersonnelSelectAdapter
import com.zarholding.zar.view.recycler.holder.PersonnelSelectHolder
import com.zarholding.zar.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.databinding.DialogPersonnelBinding


@AndroidEntryPoint
class PersonnelDialog(
    private val chooseItem: Click
) : DialogFragment() {

    lateinit var binding: DialogPersonnelBinding

    private val userViewModel: UserViewModel by viewModels()

    private var job: Job? = null

    var adapterPersonnel: PersonnelSelectAdapter? = null

    var endlessScrollListener: EndlessScrollListener? = null

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

        binding.imageViewClose.setOnClickListener { dismiss() }

        binding.textInputEditTextSearch.addTextChangedListener {
            adapterPersonnel = null
            userViewModel.filterUser.PageNumber = 0
            binding.recyclerViewPersonnel.adapter = null
            job?.cancel()
            createJobForSearch(it.toString())
        }

    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- createJobForSearch
    private fun createJobForSearch(search: String) {
        job = CoroutineScope(IO).launch {
            delay(800)
            withContext(Main) {
                if (search.isNotEmpty())
                    requestGetUser(search)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- createJobForSearch


    //---------------------------------------------------------------------------------------------- requestGetUser
    private fun requestGetUser(search: String) {
        binding.textViewLoading.visibility = View.VISIBLE
        userViewModel.requestGetUser(search)
            .observe(viewLifecycleOwner) { response ->
                binding.textViewLoading.visibility = View.GONE
                response?.let { data ->
                    if (!data.hasError) {
                        data.data?.let { item ->
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
    private fun setPersonnelAdapter(items: List<UserInfoEntity>) {
        adapterPersonnel?.let {
            adapterPersonnel?.addPerson(items)
            endlessScrollListener?.let {
                it.setLoading(false)
                if (items.isNullOrEmpty())
                    binding.recyclerViewPersonnel.removeOnScrollListener(it)
            }
        } ?: run {
            val select = object : PersonnelSelectHolder.Click {
                override fun select(item: UserInfoEntity) {
                    chooseItem.select(item)
                    dismiss()
                }
            }
            adapterPersonnel = PersonnelSelectAdapter(items.toMutableList(), select)
            val manager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            endlessScrollListener = getEndlessScrollListener(manager)
            binding.recyclerViewPersonnel.layoutManager = manager
            binding.recyclerViewPersonnel.adapter = adapterPersonnel
            binding.recyclerViewPersonnel.addOnScrollListener(endlessScrollListener!!)
        }

    }
    //---------------------------------------------------------------------------------------------- setPersonnelAdapter


    //______________________________________________________________________________________________ getEndlessScrollListener
    private fun getEndlessScrollListener(manager: LinearLayoutManager): EndlessScrollListener {
        val endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun loadMoreItems() {
                requestGetUser(userViewModel.filterUser.Search)
            }
        }
        endlessScrollListener.setLoading(false)
        return endlessScrollListener
    }
    //______________________________________________________________________________________________ getEndlessScrollListener


    //---------------------------------------------------------------------------------------------- dismiss
    override fun dismiss() {
        super.dismiss()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- dismiss
}