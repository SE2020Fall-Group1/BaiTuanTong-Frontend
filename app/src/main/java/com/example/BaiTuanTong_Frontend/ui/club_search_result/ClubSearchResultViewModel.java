package com.example.BaiTuanTong_Frontend.ui.club_search_result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClubSearchResultViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ClubSearchResultViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}