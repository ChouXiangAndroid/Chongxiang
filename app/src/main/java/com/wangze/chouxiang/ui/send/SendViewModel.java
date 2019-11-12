package com.wangze.chouxiang.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wangze.chouxiang.wangze.User;


public class  SendViewModel extends ViewModel {

    private MutableLiveData<User> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(new User());
    }

    public LiveData<User> getText() {
        return mText;
    }
}