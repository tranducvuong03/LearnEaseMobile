package com.example.mobile.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobile.R;

public class LoadingDialogFragment extends DialogFragment {
    private ImageView imageLoader;

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container, false);
        imageLoader = view.findViewById(R.id.imageLoader);
        startCoinFlipAnimation();
        return view;
    }

    private void startCoinFlipAnimation() {
        ObjectAnimator flip = ObjectAnimator.ofFloat(imageLoader, "rotationY", 0f, 360f);
        flip.setDuration(1000);
        flip.setRepeatCount(ObjectAnimator.INFINITE);
        flip.setInterpolator(new LinearInterpolator());
        flip.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}

