package com.vincent.hoangnguyen.classmanagement.ui.Information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InformationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public InformationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Information fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}