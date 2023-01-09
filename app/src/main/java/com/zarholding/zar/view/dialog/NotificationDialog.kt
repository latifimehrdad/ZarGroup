package com.zarholding.zar.view.dialog

import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.zar.core.tools.loadings.LoadingManager
import com.zarholding.zar.model.notification_signalr.NotificationSignalrModel
import com.zarholding.zar.utility.CompanionValues
import com.zarholding.zar.view.activity.MainActivity
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
    private var unReadNotifyCount = 0


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
        binding.materialButtonReadAll.setOnClickListener { readAllNotification() }
        binding.materialButtonReadChecked.setOnClickListener { readCheckedNotification() }
    }
    //---------------------------------------------------------------------------------------------- setListener


    //---------------------------------------------------------------------------------------------- showMessage
    private fun showMessage(message: String) {
        activity?.let {
            (it as MainActivity).showMessage(message)
        }
    }
    //---------------------------------------------------------------------------------------------- showMessage



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
            if (categoryPosition != -1) {
                adapter!!.setReadNotification(categoryPosition, notificationPosition)
                var count = binding.tabLayout.getTabAt(categoryPosition)?.badge?.number
                count?.let {
                    count--
                    if (count < 1)
                        binding.tabLayout.getTabAt(categoryPosition)?.removeBadge()
                    else
                        binding.tabLayout.getTabAt(categoryPosition)?.badge?.number = count
                }
                /*این برودکست برای تغییر تعداد نوتیفیکیشن در اکتیویتی است*/
                val intent = Intent("com.zarholding.zar.receive.message")
                context?.sendBroadcast(intent)
            } else
                forceGetListNotification()
        }
    }
    //---------------------------------------------------------------------------------------------- observeReadLiveData


    //---------------------------------------------------------------------------------------------- observeNotificationResponseLiveData
    private fun observeNotificationResponseLiveData() {
        notificationViewModel.notificationResponseLiveData.observe(viewLifecycleOwner) {
            loadingManager.stopLoadingView()
            for (i in 0 until binding.tabLayout.tabCount)
                binding.tabLayout.getTabAt(i)?.removeBadge()
            binding.tabLayout.removeAllTabs()
            unReadNotifyCount = 0
            for (category in it) {
                val count = category.notifications.filter { notify -> !notify.isRead }
                unReadNotifyCount+=count.size
                var tab: TabLayout.Tab
                if (count.isNotEmpty())
                    tab = binding.tabLayout.newTab().apply {
                        text = category.name
                        orCreateBadge
                        badge?.isVisible = true
                        badge?.number = count.size
                    }
                else
                    tab = binding.tabLayout.newTab().apply {
                        text = category.name
                    }
                binding.tabLayout.addTab(tab)
            }

            val click = object : NotificationItemHolder.Click {
                override fun showDetail(categoryPosition: Int, notificationPosition: Int) {
                    readItemNotification(categoryPosition, notificationPosition)
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
            (activity as MainActivity).setNotificationCount(unReadNotifyCount)
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


    //---------------------------------------------------------------------------------------------- forceGetListNotification
    private fun forceGetListNotification() {
        loadingManager.setRecyclerLoading(
            binding.recyclerViewNotification,
            R.layout.item_loading,
            R.color.recyclerLoadingShadow,
            1
        )
        notificationViewModel.forceGetListNotification()
    }
    //---------------------------------------------------------------------------------------------- forceGetListNotification


    //---------------------------------------------------------------------------------------------- readItemNotification
    private fun readItemNotification(categoryPosition: Int, notificationPosition: Int) {
        this.categoryPosition = categoryPosition
        this.notificationPosition = notificationPosition
        adapter?.let {
            val item = it
                .getListOfCategories()[categoryPosition]
                .notifications[notificationPosition]
            context?.let {
                NotificationDetailDialog(requireContext(), item).show()
            }
            requestReadNotification(listOf(item.id))
        }
    }
    //---------------------------------------------------------------------------------------------- readItemNotification


    //---------------------------------------------------------------------------------------------- requestReadNotification
    private fun requestReadNotification(ids: List<Int>) {
        notificationViewModel.requestReadNotification(ids)
    }
    //---------------------------------------------------------------------------------------------- requestReadNotification


    //---------------------------------------------------------------------------------------------- readAllNotification
    private fun readAllNotification() {
        val notify = mutableListOf<NotificationSignalrModel>()
        adapter?.let {
            val categories = it.getListOfCategories()
            for (category in categories) {
                val unReads = category.notifications.filter { notify -> !notify.isRead }
                if (unReads.isNotEmpty())
                    notify.addAll(unReads)
            }
        }
        if (notify.isEmpty())
            showMessage(getString(R.string.thereAreNoUnreadMessage))
        else
            showDialogConfirmToReadNotification(notify)
    }
    //---------------------------------------------------------------------------------------------- readAllNotification


    //---------------------------------------------------------------------------------------------- readCheckedNotification
    private fun readCheckedNotification() {
        val notify = mutableListOf<NotificationSignalrModel>()
        adapter?.let {
            val categories = it.getListOfCategories()
            for (category in categories) {
                val unReads = category.notifications
                    .filter { notify -> !notify.isRead && notify.select }
                if (unReads.isNotEmpty())
                    notify.addAll(unReads)
            }
        }
        if (notify.isEmpty())
            showMessage(getString(R.string.thereAreNoUnreadMessage))
        else
            showDialogConfirmToReadNotification(notify)
    }
    //---------------------------------------------------------------------------------------------- readCheckedNotification


    //---------------------------------------------------------------------------------------------- showDialogConfirmToReadNotification
    private fun showDialogConfirmToReadNotification(items : MutableList<NotificationSignalrModel>) {
        val click = object : ConfirmDialog.Click{
            override fun clickYes() {
                categoryPosition = -1
                val ids = items.map { it.id }
                requestReadNotification(ids)
            }
        }

        ConfirmDialog(
            requireContext(),
            ConfirmDialog.ConfirmType.WARNING,
            getString(R.string.doYouWantToReadNotification),
            click
        ).show()
    }
    //---------------------------------------------------------------------------------------------- showDialogConfirmToReadNotification


    //---------------------------------------------------------------------------------------------- onDismiss
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireContext().unregisterReceiver(broadcastReceiver)
    }
    //---------------------------------------------------------------------------------------------- onDismiss
}