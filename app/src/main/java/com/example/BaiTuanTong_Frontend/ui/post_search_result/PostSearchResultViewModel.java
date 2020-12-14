package com.example.BaiTuanTong_Frontend.ui.post_search_result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PostSearchResultViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PostSearchResultViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}