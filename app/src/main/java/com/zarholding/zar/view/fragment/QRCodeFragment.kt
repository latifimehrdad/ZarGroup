package com.zarholding.zar.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.extensions.persianNumberToEnglishNumber
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.QRCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.QRResult.QRSuccess
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import zar.R
import zar.databinding.FragmentQrcodeBinding
import javax.inject.Inject


@AndroidEntryPoint
class QRCodeFragment : Fragment() {

    @Inject
    lateinit var loadingManager: LoadingManager

    private var _binding: FragmentQrcodeBinding? = null
    private val binding get() = _binding!!

    private val qrCodeViewModel: QRCodeViewModel by viewModels()

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setListener()
        observeErrorLiveDate()
        observeSuccessLiveData()
        startQRCodeReader()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
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
        qrCodeViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
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
        qrCodeViewModel.successLiveData.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            binding.item = it
        }
    }
    //---------------------------------------------------------------------------------------------- observeSuccessLiveData


    //---------------------------------------------------------------------------------------------- handleResult
    private fun handleResult(result: QRResult) {
        when (result) {
            is QRSuccess -> {
                result.content.rawValue
                requestUserInfo(result.content.rawValue)
            }
            else -> {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- handleResult


    //---------------------------------------------------------------------------------------------- requestUserInfo
    private fun requestUserInfo(id: String?) {
        loadingManager.setViewLoading(
            binding.linearLayoutContent,
            R.layout.item_loading_view,
            R.color.recyclerLoadingShadow
        )
        if (id?.isDigitsOnly() == true)
            qrCodeViewModel.requestUserInfo(id.toInt())
    }
    //---------------------------------------------------------------------------------------------- requestUserInfo


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView

}