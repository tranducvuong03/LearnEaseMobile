package com.example.mobile.dialog;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.adapter.SampleAdapter;
import com.example.mobile.model.SpeakingDialect;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.List;

public class SampleDialogFragment extends BottomSheetDialogFragment {
    private RecyclerView recyclerSamples;
    private List<SpeakingDialect> samples;

    public SampleDialogFragment(List<SpeakingDialect> sampleList) {
        this.samples = sampleList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_voice_sample, container, false);

        recyclerSamples = view.findViewById(R.id.recyclerSamples);
        recyclerSamples.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSamples.setAdapter(new SampleAdapter(samples));

        return view;
    }
}