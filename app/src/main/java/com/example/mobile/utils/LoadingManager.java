package com.example.mobile.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class LoadingManager {
    private static LoadingManager instance;
    private LoadingDialogFragment dialog;
    private long showStartTime = 0;
    private final long MIN_DISPLAY_TIME = 1000; // ms
    private boolean isDialogShown = false;

    private LoadingManager() {}

    public static synchronized LoadingManager getInstance() {
        if (instance == null) {
            instance = new LoadingManager();
        }
        return instance;
    }

    public void show(Activity activity) {
        if (!(activity instanceof FragmentActivity)) return;
        FragmentActivity fragmentActivity = (FragmentActivity) activity;

        if (fragmentActivity.isFinishing() || fragmentActivity.isDestroyed()) return;
        if (dialog != null && dialog.isAdded()) return;

        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        if (fm.isStateSaved()) return;

        showStartTime = System.currentTimeMillis();
        isDialogShown = true;

        new Handler(Looper.getMainLooper()).post(() -> {
            if (fragmentActivity.isFinishing() || fragmentActivity.isDestroyed()) return;

            dialog = LoadingDialogFragment.newInstance();
            dialog.setCancelable(false);

            try {
                dialog.show(fm, "loading");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });
    }

    public void dismiss() {
        if (!isDialogShown) return;

        long elapsed = System.currentTimeMillis() - showStartTime;
        long delay = Math.max(MIN_DISPLAY_TIME - elapsed, 0);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog != null && dialog.isAdded()) {
                try {
                    dialog.dismissAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog = null;
            }
            isDialogShown = false;
        }, delay);
    }
}



