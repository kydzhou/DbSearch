package com.dbsearch.app.listener.bmob;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

public class FindListenerImpl<T> extends FindListener<T> {
    @Override
    public void onSuccess(List<T> list) {

    }

    @Override
    public void onError(int i, String s) {
    }
}
