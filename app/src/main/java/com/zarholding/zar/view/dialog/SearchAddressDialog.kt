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
import com.zarholding.zar.model.response.address.AddressSuggestionModel
import com.zarholding.zar.view.recycler.adapter.AddressSuggestionAdapter
import com.zarholding.zar.view.recycler.holder.AddressSuggestionItemHolder
import com.zarholding.zar.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zar.R
import zar.databinding.DialogSearchAddressBinding
import javax.inject.Inject


@AndroidEntryPoint
class SearchAddressDialog(
    private val chooseItem: Click
) : DialogFragment() {

    lateinit var binding: DialogSearchAddressBinding

    @Inject
    lateinit var loadingManager : LoadingManager

    private val addressViewModel: AddressViewModel by viewModels()
    private var job: Job? = null
    private var adapter : AddressSuggestionAdapter? = null


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun select(item: AddressSuggestionModel)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchAddressBinding.inflate(inflater, container, false)
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
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        binding.linearLayoutLoading.visibility = View.GONE
        setListener()
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.imageViewClose.setOnClickListener { dismiss() }
        binding.textInputEditTextAddress.addTextChangedListener { checkEmptyValueForSearch() }
        binding.buttonLoadMore.setOnClickListener { requestGetUser(
            binding.textInputEditTextAddress.text.toString()) }
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- checkEmptyValueForSearch
    private fun checkEmptyValueForSearch() {

        job?.cancel()

        adapter = null
        binding.buttonLoadMore.visibility = View.GONE
        binding.recyclerViewSuggestion.adapter = null

/*        if (binding.textInputEditTextCity.text.isNullOrEmpty()) {
            binding.textInputEditTextCity.error = getString(R.string.cityIsEmpty)
            return
        }*/
        if (binding.textInputEditTextAddress.text.isNullOrEmpty()) {
            binding.textInputEditTextAddress.error = getString(R.string.addressIsEmpty)
            return
        }
        createJobForSearch(
            binding.textInputEditTextAddress.text.toString()
        )
    }
    //---------------------------------------------------------------------------------------------- checkEmptyValueForSearch


    //---------------------------------------------------------------------------------------------- createJobForSearch
    private fun createJobForSearch(address: String) {
        job = CoroutineScope(IO).launch {
            delay(1000)
            withContext(Main) {
                requestGetUser(address)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- createJobForSearch


    //---------------------------------------------------------------------------------------------- requestGetUser
    private fun requestGetUser(
        address: String
    ) {
        startLoading()
        addressViewModel.requestGetSuggestionAddress(address, adapter?.getList())
            .observe(viewLifecycleOwner) { response ->
                stopLoading()
                response?.let { items ->
                    setAdapter(items)
                }
            }
    }
    //---------------------------------------------------------------------------------------------- requestGetUser


    //---------------------------------------------------------------------------------------------- setAdapter
    private fun setAdapter(items : List<AddressSuggestionModel>) {
        loadingManager.stopLoadingView()
        val select = object : AddressSuggestionItemHolder.Click {
            override fun selectItem(item : AddressSuggestionModel) {
                chooseItem.select(item)
                dismiss()
            }
        }
        adapter?.let {
            adapter!!.addAddress(items)
        } ?: run {
            adapter = AddressSuggestionAdapter(items.toMutableList(), select)
            val manager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.recyclerViewSuggestion.layoutManager = manager
            binding.recyclerViewSuggestion.adapter = adapter
        }
    }
    //---------------------------------------------------------------------------------------------- setAdapter


    //---------------------------------------------------------------------------------------------- startLoading
    private fun startLoading() {
        binding.linearLayoutLoading.visibility = View.VISIBLE
        binding.buttonLoadMore.visibility = View.GONE
        loadingManager.setViewLoading(
            binding.linearLayoutLoading,
            R.layout.item_loading_light,
            R.color.recyclerLoadingShadow
        )
        binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
    }
    //---------------------------------------------------------------------------------------------- startLoading



    //---------------------------------------------------------------------------------------------- stopLoading
    private fun stopLoading() {
        binding.buttonLoadMore.visibility = View.VISIBLE
        binding.linearLayoutLoading.visibility = View.GONE
        loadingManager.stopLoadingView()
    }
    //---------------------------------------------------------------------------------------------- stopLoading



    //---------------------------------------------------------------------------------------------- dismiss
    override fun dismiss() {
        super.dismiss()
        job?.cancel()
    }
    //---------------------------------------------------------------------------------------------- dismiss
}