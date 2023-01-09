package com.zarholding.zar.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.powerspinner.DefaultSpinnerAdapter
import com.zar.core.enums.EnumApiError
import com.zarholding.zar.view.extension.hideKeyboard
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.ParkingViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.DialogEditPlaqueBinding


@AndroidEntryPoint
class EditPlaqueDialog(private val click: Click) : DialogFragment() {

    lateinit var binding: DialogEditPlaqueBinding

    private val parkingViewModel: ParkingViewModel by viewModels()

    interface Click {
        fun editPlaque()
    }

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEditPlaqueBinding.inflate(inflater, container, false)
        binding.viewModel = parkingViewModel
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        val lp = WindowManager.LayoutParams()
        val window = dialog!!.window
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        window!!.setBackgroundDrawable(inset)
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.horizontalMargin = 50f
        window.attributes = lp
        parkingViewModel.initUserCarPlaque()
        observeSuccessLiveData()
        observeErrorLiveDate()
        setListener()
        initAlphabetSpinner()
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {
        binding.imageViewClose.setOnClickListener { dismiss() }
        binding.buttonYes.setOnClickListener { requestChangeCarPlaque() }
        binding.buttonNo.setOnClickListener { dismiss() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- initAlphabetSpinner
    private fun initAlphabetSpinner() {
        binding.powerSpinnerAlphabet.apply {
            setSpinnerAdapter(DefaultSpinnerAdapter(this))
            setItems(parkingViewModel.getAlphabet())
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner
        }
        binding.powerSpinnerAlphabet.selectItemByIndex(0)
    }
    //---------------------------------------------------------------------------------------------- initAlphabetSpinner


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        parkingViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            binding.buttonYes.stopLoading()
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()

                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate


    //---------------------------------------------------------------------------------------------- observeSuccessLiveData
    private fun observeSuccessLiveData() {
        parkingViewModel.successLiveData.observe(viewLifecycleOwner) {
            click.editPlaque()
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- observeSuccessLiveData


    //---------------------------------------------------------------------------------------------- requestChangeCarPlaque
    private fun requestChangeCarPlaque() {
        if (binding.buttonYes.isLoading)
            return
        hideKeyboard()
        binding.buttonYes.startLoading(getString(R.string.bePatient))
        parkingViewModel.requestChangeCarPlaque()
    }
    //---------------------------------------------------------------------------------------------- requestChangeCarPlaque

}