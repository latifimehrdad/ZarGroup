package com.zarholding.zar.view.dialog

import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.gson.Gson
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.recycler.adapter.notification.NotificationCategoryAdapter
import com.zarholding.zar.view.recycler.holder.notification.NotificationItemHolder
import com.zarholding.zar.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import zar.R
import zar.databinding.DialogNotificationBinding
import javax.inject.Inject


@AndroidEntryPoint
class NotificationDialog(
    private val margin: Int
) : DialogFragment() {

    @Inject
    lateinit var loadingManager: LoadingManager

    lateinit var binding: DialogNotificationBinding

    private val notificationViewModel: NotificationViewModel by viewModels()

    private var adapter: NotificationCategoryAdapter? = null

    private var broadcastReceiver: BroadcastReceiver? = null

    private var categoryPosition = 0
    private var notificationPosition = 0


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val height: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics? =
                context?.getSystemService(WindowManager::class.java)?.currentWindowMetrics
            metrics?.bounds?.let {
                it.height() - margin - (margin / 2)
            } ?: run {
                WindowManager.LayoutParams.MATCH_PARENT
            }
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            displayMetrics.heightPixels - margin - (margin / 2)
        }

        isCancelable = false
        val lp = WindowManager.LayoutParams()
        val window = dialog!!.window
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        window!!.setBackgroundDrawable(inset)
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = height
        lp.gravity = Gravity.TOP
        lp.horizontalMargin = 50f
        lp.y = margin
        window.attributes = lp
        registerReceiver()
        setListener()
        observeReadLiveData()
        observeNotificationResponseLiveData()
        getNotification()
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- setListener
    private fun setListener() {

        binding.imageViewClose.setOnClickListener { dismiss() }

    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- registerReceiver
    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val item = intent.getStringExtra(CompanionValues.notificationLast)
                if (item.isNullOrEmpty())
                    return
                val gson = Gson()
                val notification = gson.fromJson(item, NotificationSignalrModel::class.java)
                notificationViewModel.addNotification(notification)
            }
        }
        requireContext().registerReceiver(
            broadcastReceiver,
            IntentFilter("com.zarholding.zar.receive.message")
        )
    }
    //---------------------------------------------------------------------------------------------- registerReceiver


    //---------------------------------------------------------------------------------------------- observeReadLiveData
    private fun observeReadLiveData() {
        notificationViewModel.readLiveData.observe(viewLifecycleOwner) {
            Log.i("meri", "selectedTabPosition = ${binding.tabLayout.selectedTabPosition}")
            adapter!!.setReadNotification(categoryPosition, notificationPosition)
            val intent = Intent("com.zarholding.zar.receive.message")
            context?.sendBroadcast(intent)
        }
    }
    //---------------------------------------------------------------------------------------------- observeReadLiveData


    //---------------------------------------------------------------------------------------------- observeNotificationResponseLiveData
    private fun observeNotificationResponseLiveData() {
        notificationViewModel.notificationResponseLiveData.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            binding.tabLayout.removeAllTabs()

            for (category in it) {
                val tab = binding.tabLayout.newTab().apply {
                    text = category.name
                    orCreateBadge
                    badge?.isVisible = true
                    badge?.number = category.notifications.size
                }
                binding.tabLayout.addTab(tab)
            }

            val click = object : NotificationItemHolder.Click {
                override fun showDetail(categoryPosition: Int, notificationPosition: Int) {
                    requestReadNotification(categoryPosition, notificationPosition)
                }
            }

            adapter = NotificationCategoryAdapter(it, click)
            binding.recyclerViewNotification.adapter = adapter

            TabbedListMediator(
                binding.recyclerViewNotification,
                binding.tabLayout,
                it.indices.toList(),
                true
            ).attach()
        }
    }
    //---------------------------------------------------------------------------------------------- observeNotificationResponseLiveData


    //---------------------------------------------------------------------------------------------- getNotification
    private fun getNotification() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewNotification,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
        notificationViewModel.getNotification()
    }
    //---------------------------------------------------------------------------------------------- getNotification


    //---------------------------------------------------------------------------------------------- requestReadNotification
    private fun requestReadNotification(categoryPosition: Int, notificationPosition: Int) {
        this.categoryPosition = categoryPosition
        this.notificationPosition = notificationPosition
        adapter?.let {
            val item = it
                .getListOfCategories()[categoryPosition]
                .notifications[notificationPosition]
            context?.let {
                NotificationDetailDialog(requireContext(), item).show()
            }
            notificationViewModel.requestReadNotification(listOf(item.id))
        }

    }
    //---------------------------------------------------------------------------------------------- requestReadNotification


    //---------------------------------------------------------------------------------------------- onDismiss
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireContext().unregisterReceiver(broadcastReceiver)
    }
    //---------------------------------------------------------------------------------------------- onDismiss
}