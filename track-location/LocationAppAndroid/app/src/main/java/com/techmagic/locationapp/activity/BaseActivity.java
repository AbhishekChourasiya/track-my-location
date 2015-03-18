package com.techmagic.locationapp.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;

import com.techmagic.locationapp.TrackLocationApplication;
import com.techmagic.locationapp.data.DataHelper;

import co.techmagic.hi.R;

public class BaseActivity extends ActionBarActivity {

    protected DataHelper dataHelper;
    protected TrackLocationApplication app;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (TrackLocationApplication) getApplication();
        dataHelper = DataHelper.getInstance(getApplicationContext());
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.START);
    }

    protected void initDrawerToggle(DrawerLayout drawerLayout, int text) {
        this.drawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                text,
                text
        );

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
