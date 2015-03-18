package com.techmagic.locationapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.techmagic.locationapp.TrackLocationApplication;
import com.techmagic.locationapp.data.DataHelper;

public class BaseActivity extends ActionBarActivity {

    protected DataHelper dataHelper;
    protected TrackLocationApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (TrackLocationApplication) getApplication();
        dataHelper = DataHelper.getInstance(getApplicationContext());
    }
}
