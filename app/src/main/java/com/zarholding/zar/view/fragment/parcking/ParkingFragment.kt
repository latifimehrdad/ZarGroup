package com.zarholding.zar.view.fragment.parcking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.powerspinner.DefaultSpinnerAdapter
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zarholding.zar.view.extension.hideKeyboard
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.ParkingViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.QRResult.QRSuccess
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import zar.R
import zar.databinding.FragmentParkingBinding


@AndroidEntryPoint
class ParkingFragment : Fragment() {

    private var _binding: FragmentParkingBinding? = null
    private val binding get() = _binding!!

    private val parkingViewModel: ParkingViewModel by viewModels()

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParkingBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = parkingViewModel
        binding.token = parkingViewModel.getBearerToken()
        setListener()
        initAlphabetSpinner()
        observeErrorLiveDate()
        observeSuccessLiveData()
//        startQRCodeReader()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
        binding.materialButtonSearch.stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage


    //---------------------------------------------------------------------------------------------- startQRCodeReader
    private fun startQRCodeReader() {
        val scanCustomCode =
            registerForActivityResult(ScanCustomCode()) { result -> handleResult(result) }
        scanCustomCode.launch(
            ScannerConfig.build {
                setOverlayStringRes(R.string.choose) // string resource used for the scanner overlay
                setOverlayDrawableRes(R.drawable.ic_cab) // drawable resource used for the scanner overlay
                setShowTorchToggle(true)
                setUseFrontCamera(false) // use the front camera
            }
        )
    }
    //---------------------------------------------------------------------------------------------- startQRCodeReader


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.materialButtonSearch.setOnClickListener { requestGetUserInfo() }

        binding.imageViewRetry.setOnClickListener { resetSearch() }

        binding.materialButtonSms.setOnClickListener {
            binding.item?.let {
                val number = it.mobile.persianNumberToEnglishNumber()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.fromParts("sms", number, null)
                    )
                )
            }
        }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        parkingViewModel.errorLiveDate.observe(viewLifecycleOwner) {
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
            binding.materialButtonSearch.visibility = View.INVISIBLE
            binding.imageViewRetry.visibility = View.VISIBLE
            binding.materialButtonSearch.stopLoading()
            binding.item = it
            binding.expandableInfo.expand()
        }
    }
    //---------------------------------------------------------------------------------------------- observeSuccessLiveData


    //---------------------------------------------------------------------------------------------- handleResult
    private fun handleResult(result: QRResult) {
        when (result) {
            is QRSuccess -> {
                result.content.rawValue
//                requestUserInfo(result.content.rawValue)
            }
            else -> {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- handleResult


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


    //---------------------------------------------------------------------------------------------- resetSearch
    private fun resetSearch() {
        if (binding.materialButtonSearch.isLoading)
            return
        binding.materialButtonSearch.visibility = View.VISIBLE
        binding.imageViewRetry.visibility = View.GONE
        binding.expandableInfo.requestFocus()
        binding.expandableInfo.collapse()
        binding.editTextNumber1.text.clear()
        binding.editTextNumber2.text.clear()
        binding.editTextCityCode.text.clear()
        binding.powerSpinnerAlphabet.selectItemByIndex(0)
        binding.powerSpinnerAlphabet.showArrow = true
        binding.powerSpinnerAlphabet.setBackgroundResource(R.drawable.drawable_border_car_number)
    }
    //---------------------------------------------------------------------------------------------- resetSearch


    //---------------------------------------------------------------------------------------------- requestGetUserInfo
    private fun requestGetUserInfo() {
        if (binding.materialButtonSearch.isLoading)
            return
        hideKeyboard()
        binding.expandableInfo.collapse()
        binding.materialButtonSearch.startLoading(getString(R.string.bePatient))
        parkingViewModel.requestGetUserInfo()
    }
    //---------------------------------------------------------------------------------------------- requestGetUserInfo



    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}