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
import com.zarholding.zar.database.entity.ArticleEntity
import zar.databinding.DialogConfirmBinding
import zar.databinding.DialogDetailNewsBinding

class ConfirmDialog(
    private val title: String,
    private val icon: Int,
    private val click: Click
) : DialogFragment() {

    private lateinit var binding: DialogConfirmBinding

    interface Click {
        fun clickYes()
    }

    //---------------------------------------------------------------------------------------------- onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogConfirmBinding.inflate(inflater, container, false)
        binding.title = title
        binding.icon = icon
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
        setonListener()
    }
    //---------------------------------------------------------------------------------------------- onCreateView


    //---------------------------------------------------------------------------------------------- setonListener
    private fun setonListener() {
        binding.buttonNo.setOnClickListener {
            dismiss()
        }

        binding.buttonYes.setOnClickListener {
            click.clickYes()
            dismiss()
        }
    }
    //---------------------------------------------------------------------------------------------- setonListener

}