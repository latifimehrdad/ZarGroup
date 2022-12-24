package com.zarholding.zar.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

public class ZarButton extends MaterialButton {

    private boolean loading = false;
    private String defaultTitle;


    //---------------------------------------------------------------------------------------------- ZarButton
    public ZarButton(@NonNull Context context) {
        super(context);
    }
    //---------------------------------------------------------------------------------------------- ZarButton


    //---------------------------------------------------------------------------------------------- ZarButton
    public ZarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //---------------------------------------------------------------------------------------------- ZarButton


    //---------------------------------------------------------------------------------------------- ZarButton
    public ZarButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //---------------------------------------------------------------------------------------------- ZarButton


    //---------------------------------------------------------------------------------------------- stopLoading
    public void stopLoading() {
        loading = false;
        setText(defaultTitle);
    }
    //---------------------------------------------------------------------------------------------- stopLoading


    //---------------------------------------------------------------------------------------------- startLoading
    public void startLoading(String title) {
        defaultTitle = getText().toString();
        loading = true;
        setText(title);
    }
    //---------------------------------------------------------------------------------------------- startLoading


    //---------------------------------------------------------------------------------------------- isLoading
    public Boolean isLoading() {
        return loading;
    }
    //---------------------------------------------------------------------------------------------- isLoading


}
