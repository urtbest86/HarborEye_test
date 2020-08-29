package com.example.harboreye.ui.CCTV;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.harboreye.LiveVideoActivity;
import com.example.harboreye.R;

public class CCTVFragment extends Fragment implements View.OnClickListener {

    private Button button1;
    private Button button2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //가로 화면 유지

       View root = inflater.inflate(R.layout.fragment_cctv, container, false);
       button1=root.findViewById(R.id.button1);
       button1.setOnClickListener(this);

        button2=root.findViewById(R.id.button2);
        button2.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(), LiveVideoActivity.class);
        String selecturl = null;
        switch (v.getId()){
            case R.id.button1:
                selecturl=getString(R.string.button1_url);
                break;
            case R.id.button2:
                selecturl=getString(R.string.button2_url);
                break;
            default:
               break;
        }
        intent.putExtra("url",selecturl);
        startActivity(intent);
    }
}