package com.example.gkudva.android_gtwitter.presenter;

/**
 * Created by gkudva on 28/09/17.
 */

public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}
