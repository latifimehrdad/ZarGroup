package com.zarholding.zar.utility;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager layoutManager;
    public boolean isLoading;

    public EndlessScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }


    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // check for scroll down
        if (dy > 0) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading)
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true;
                    loadMoreItems();
                }
        }
    }

    protected abstract void loadMoreItems();

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView view, int scrollState) {
        // Don't take any action on changed
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}