package com.example.gkudva.android_gtwitter.view;

import com.example.gkudva.android_gtwitter.model.Tweet;

import java.util.List;

/**
 * Created by gkudva on 28/09/17.
 */

public interface TimelineMvpView extends MvpView {

    void showTimeline(List<Tweet> timelineList);

    void showMessage(String message);

//    void showProgressIndicator();
}