package com.zarholding.zar.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumApiError
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.enum.EnumDriverType
import com.zarholding.zar.model.response.taxi.AdminTaxiRequestModel
import com.zarholding.zar.view.activity.MainActivity
import com.zarholding.zar.viewmodel.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.DialogDriverBinding
import javax.inject.Inject

@AndroidEntryPoint
class DriverDialog(
    private val click: Click,
    private val item: AdminTaxiRequestModel
) : DialogFragment() {

    @Inject
    lateinit var loadingManager: LoadingManager

    lateinit var binding: DialogDriverBinding

    private val driverViewModel : DriverViewModel by viewModels()


    //---------------------------------------------------------------------------------------------- Click
    interface Click {
        fun clickYes(message : String)
    }
    //---------------------------------------------------------------------------------------------- Click


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDriverBinding.inflate(inflater, container, false)
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
        initView()
        setListener()
        val title = getString(R.string.taxiRequestIsConfirm, item.requesterName)
        binding.textViewTitle.text = title
    }
    //---------------------------------------------------------------------------------------------- onCreateView



    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {
        observeErrorLiveDate()
        binding.powerSpinnerDriver.visibility = View.GONE
        binding.textViewDriver.visibility = View.GONE
        initPowerSpinnerTaxiType()
        observeDriversListLiveData()
        observeAssignDriverLiveData()
    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, context?.theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, context?.theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, context?.theme))
        snack.show()
        binding.buttonYes.stopLoading()
    }
    //---------------------------------------------------------------------------------------------- showMessage



    //---------------------------------------------------------------------------------------------- observeLoginLiveDate
    private fun observeErrorLiveDate() {
        driverViewModel.errorLiveDate.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            showMessage(it.message)
            when (it.type) {
                EnumApiError.UnAuthorization -> (activity as MainActivity?)?.gotoFirstFragment()

                else -> {}
            }
        }
    }
    //---------------------------------------------------------------------------------------------- observeLoginLiveDate



    //---------------------------------------------------------------------------------------------- observeDriversListLiveData
    private fun observeDriversListLiveData() {
        driverViewModel.driversListLiveData.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            initPowerSpinnerDriver()
        }
    }
    //---------------------------------------------------------------------------------------------- observeDriversListLiveData


    //---------------------------------------------------------------------------------------------- observeAssignDriverLiveData
    private fun observeAssignDriverLiveData() {
        driverViewModel.assignDriverLiveData.observe(viewLifecycleOwner) {
            click.clickYes(it)
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- observeAssignDriverLiveData



    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.imageViewClose.setOnClickListener { dismiss() }

        binding.buttonNo.setOnClickListener { dismiss() }

        binding.buttonYes.setOnClickListener { requestAssignDriverToRequest() }

    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- initPowerSpinnerTaxiType
    private fun initPowerSpinnerTaxiType() {
        val items = listOf(
            IconSpinnerItem(getString(R.string.companyDriver)),
            IconSpinnerItem(getString(R.string.agencyDriver)))

        binding.powerSpinnerType.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(items)
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner

            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, _ ->
                binding.powerSpinnerType.setBackgroundResource(R.drawable.drawable_spinner_select)
                val type = if (newIndex == 0)
                    EnumDriverType.Personnel
                else
                    EnumDriverType.Agency
                getDriver(type)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initPowerSpinnerTaxiType



    //---------------------------------------------------------------------------------------------- initPowerSpinnerDriver
    private fun initPowerSpinnerDriver() {
        driverViewModel.getDriverList()?.let {
            val items = it.map { driver ->
                driver.fullName?.let { name ->
                    IconSpinnerItem(name)
                }
            }
            binding.powerSpinnerDriver.apply {
                setSpinnerAdapter(IconSpinnerAdapter(this))
                setItems(items)
                getSpinnerRecyclerView().layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                lifecycleOwner = viewLifecycleOwner

                setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, newIndex, _ ->
                    driverViewModel.selectDriverByIndex(newIndex)
                    binding.powerSpinnerDriver
                        .setBackgroundResource(R.drawable.drawable_spinner_select)
                }
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initPowerSpinnerDriver



    //---------------------------------------------------------------------------------------------- getDriver
    private fun getDriver(type : EnumDriverType) {
        binding.powerSpinnerDriver.visibility = View.VISIBLE
        binding.textViewDriver.visibility = View.VISIBLE
        loadingManager.setViewLoading(
            binding.powerSpinnerDriver,
            R.layout.item_loading_view,
            R.color.recyclerLoadingShadow
        )
        driverViewModel.requestGetDriver(type, item.fromCompany)
    }
    //---------------------------------------------------------------------------------------------- getDriver



    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest
    private fun requestAssignDriverToRequest() {
        if (driverViewModel.selectedDriver == null) {
            showMessage(getString(R.string.pleaseSelectDriver))
            return
        }
        binding.buttonYes.startLoading(getString(R.string.bePatient))
        driverViewModel.requestAssignDriverToRequest(item.id.toString())
    }
    //---------------------------------------------------------------------------------------------- requestAssignDriverToRequest

}