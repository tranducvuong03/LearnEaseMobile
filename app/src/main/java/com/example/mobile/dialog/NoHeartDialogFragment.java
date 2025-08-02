package com.example.mobile.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.mobile.R;
import com.example.mobile.ShopActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NoHeartDialogFragment extends BottomSheetDialogFragment {
    public static void show(FragmentManager fragmentManager) {
        NoHeartDialogFragment dialog = new NoHeartDialogFragment();
        dialog.setCancelable(false);
        dialog.show(fragmentManager, "no_heart_dialog");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_no_heart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnCloseDialog = view.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), ShopActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.setBackgroundResource(android.R.color.transparent); // ✅ Xóa nền mặc định của BottomSheet
            }
        }

        View dialogView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (dialogView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(dialogView);

            // Đặt chiều cao tối đa theo phần trăm màn hình
            dialogView.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6); // 60%
            dialogView.requestLayout();

            // Mở rộng bottom sheet ngay khi hiển thị
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


}

