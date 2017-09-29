package com.example.gkudva.android_gtwitter.presenter;

import com.example.gkudva.android_gtwitter.view.TimelineMvpView;

import rx.Subscription;

/**
 * Created by gkudva on 28/09/17.
 */

public class TimelinePresenter implements Presenter<TimelineMvpView> {

    private TimelineMvpView mainMvpView;
    private List<Doc> mDocList;
    private static final String TAG = "TimelinePresenter";

    @Override
    public void attachView(TimelineMvpView view) {
        this.mainMvpView = view;

    }

    @Override
    public void detachView() {
        this.mainMvpView = null;
    }
}
