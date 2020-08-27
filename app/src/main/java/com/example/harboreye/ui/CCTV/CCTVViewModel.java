package com.example.harboreye.ui.CCTV;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CCTVViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CCTVViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}