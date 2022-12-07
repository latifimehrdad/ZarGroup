package com.zarholding.zar.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zar.core.tools.loadings.LoadingManager
import com.zar.core.tools.manager.ThemeManager
import com.zarholding.zar.database.dao.UserInfoDao
import com.zarholding.zar.utility.OsmManager
import com.zarholding.zar.view.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zar.R
import zar.databinding.FragmentTaxiBinding
import javax.inject.Inject


/**
 * Created by m-latifi on 11/19/2022.
 */

@AndroidEntryPoint
class TaxiReservationFragment : Fragment(), RemoteErrorEmitter {

    private var _binding: FragmentTaxiBinding? = null
    private val binding get() = _binding!!

    private val loadingManager = LoadingManager()
    private lateinit var osmManager: OsmManager

    @Inject
    lateinit var themeManagers: ThemeManager

    @Inject
    lateinit var userInfoDao: UserInfoDao


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.remoteErrorEmitter = this
        _binding = FragmentTaxiBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        osmManager = OsmManager(binding.mapView)
        osmManager.mapInitialize(themeManagers.applicationTheme())
        initView()
    }
    //---------------------------------------------------------------------------------------------- onViewCreated


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        val snack = Snackbar.make(binding.constraintLayoutParent, message, 5 * 1000)
        snack.setBackgroundTint(resources.getColor(R.color.primaryColor, requireContext().theme))
        snack.setTextColor(resources.getColor(R.color.textViewColor3, requireContext().theme))
        snack.setAction(getString(R.string.dismiss)) { snack.dismiss() }
        snack.setActionTextColor(resources.getColor(R.color.textViewColor1, requireContext().theme))
        snack.show()
        loadingManager.stopLoadingRecycler()
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        loadingManager.stopLoadingRecycler()
    }
    //---------------------------------------------------------------------------------------------- unAuthorization


    //---------------------------------------------------------------------------------------------- initView
    private fun initView() {

        initApplicatorTextView()
        initOriginSpinner()
        initDestinationSpinner()

    }
    //---------------------------------------------------------------------------------------------- initView


    //---------------------------------------------------------------------------------------------- initApplicatorTextView
    private fun initApplicatorTextView() {
        CoroutineScope(IO).launch {
            val user = userInfoDao.getUserInfo()
            withContext(Main) {
                binding.textViewApplicator.text = getString(R.string.applicator, user?.fullName)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- initApplicatorTextView


    //---------------------------------------------------------------------------------------------- initOriginSpinner
    private fun initOriginSpinner() {
        binding.powerSpinnerOrigin.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("کارخانه زر ماکارون"),
                    IconSpinnerItem("کارخانه زر نام"),
                    IconSpinnerItem("کارخانه زر کام")
                )
            )
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner
        }

        binding.powerSpinnerOrigin
            .setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
                binding.powerSpinnerOrigin.setBackgroundResource(R.drawable.drawable_spinner_select)
                binding.textViewOrigin.text = newItem.text
                binding.textViewOrigin.setTextColor(
                    resources.getColor(
                        R.color.primaryColor,
                        context?.theme
                    )
                )
            }
    }
    //---------------------------------------------------------------------------------------------- initOriginSpinner



    //---------------------------------------------------------------------------------------------- initDestinationSpinner
    private fun initDestinationSpinner() {
        binding.powerSpinnerDestination.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("کارخانه زر ماکارون"),
                    IconSpinnerItem("کارخانه زر نام"),
                    IconSpinnerItem("کارخانه زر کام")
                )
            )
            getSpinnerRecyclerView().layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            lifecycleOwner = viewLifecycleOwner
        }

        binding.powerSpinnerDestination
            .setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
                binding.powerSpinnerDestination.setBackgroundResource(R.drawable.drawable_spinner_select)
                binding.textViewDestination.text = newItem.text
                binding.textViewDestination.setTextColor(
                    resources.getColor(
                        R.color.primaryColor,
                        context?.theme
                    )
                )
            }
    }
    //---------------------------------------------------------------------------------------------- initDestinationSpinner


    //---------------------------------------------------------------------------------------------- onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onPause()
        _binding = null
    }
    //---------------------------------------------------------------------------------------------- onDestroyView


}