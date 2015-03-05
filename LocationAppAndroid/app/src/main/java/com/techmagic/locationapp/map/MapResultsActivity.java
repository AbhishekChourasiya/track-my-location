package com.techmagic.locationapp.map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techmagic.locationapp.R;
import com.techmagic.locationapp.Utils;
import com.techmagic.locationapp.data.DataHelper;
import com.techmagic.locationapp.data.model.LocationData;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MapResultsActivity extends ActionBarActivity {

    private static final int ZOOM_LEVEL = 15;
    private MapFragment mapFragment;
    private Map<String, Integer> filterData;
    private String[] items;

    private int dayFrom = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int monthFrom = Calendar.getInstance().get(Calendar.MONTH);
    private int yearFrom = Calendar.getInstance().get(Calendar.YEAR);
    private int minuteFrom = 0;
    private int hourFrom = 0;

    private int dayTo = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int monthTo = Calendar.getInstance().get(Calendar.MONTH);
    private int yearTo = Calendar.getInstance().get(Calendar.YEAR);
    private int minuteTo = 59;
    private int hourTo = 23;

    private FilterMode filterMode;

    @InjectView(R.id.btn_date_from) Button btnDateFrom;
    @InjectView(R.id.btn_time_from) Button btnTimeFrom;
    @InjectView(R.id.btn_date_to) Button btnDateTo;
    @InjectView(R.id.btn_time_to) Button btnTimeTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.inject(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        initFilterData();
        showData();
        refreshTimeDisplayed();
        refreshDateDisplayed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_results, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_filter) {
//            showFilterDialog();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void showData(int lastMilliSeconds) {
        List<LocationData> locations = null;
        if (lastMilliSeconds == 0) {
            locations = DataHelper.getInstance(getApplicationContext()).getAllLocations();
        } else {
            locations = DataHelper.getInstance(getApplicationContext()).getLastLocations(lastMilliSeconds);
        }

        showMarkersOnMap(locations);
    }

    private void showData() {
        long from = 0;
        long to = System.currentTimeMillis();

        Calendar calendarFrom = Calendar.getInstance();
        if (yearFrom != 0) {
            calendarFrom.set(Calendar.YEAR, yearFrom);
            calendarFrom.set(Calendar.MONTH, monthFrom);
            calendarFrom.set(Calendar.DAY_OF_MONTH, dayFrom);
        }
        calendarFrom.set(Calendar.HOUR_OF_DAY, hourFrom);
        calendarFrom.set(Calendar.MINUTE, minuteFrom);
        from = calendarFrom.getTime().getTime();

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.set(Calendar.YEAR, yearTo);
        calendarTo.set(Calendar.MONTH, monthTo);
        calendarTo.set(Calendar.DAY_OF_MONTH, dayTo);
        calendarTo.set(Calendar.HOUR_OF_DAY, hourTo);
        calendarTo.set(Calendar.MINUTE, minuteTo);
        to = calendarTo.getTime().getTime();

        List<LocationData> locations = null;
        locations = DataHelper.getInstance(getApplicationContext()).getLastLocations(from, to);

        showMarkersOnMap(locations);
    }

    private void showMarkersOnMap(List<LocationData> locations) {
        mapFragment.getMap().clear();

        if (locations == null || !(locations.size() > 0)) {
            Toast.makeText(this, "No locations to display", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationData locationToZoom = locations.get(0);
        mapFragment.getMap().moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(locationToZoom.getLatitude(), locationToZoom.getLongitude()), ZOOM_LEVEL));

        for (LocationData d : locations) {
            MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(d.getLatitude(), d.getLongitude()));
            mapFragment.getMap().addMarker(mo).setTitle(Utils.formatDateAndTime(d.getTimestamp()));
        }
    }

    private void initFilterData() {
        filterData = new HashMap<String, Integer>();
        filterData.put(getString(R.string.one_hour), Integer.valueOf(60 * 60));
        filterData.put(getString(R.string.one_day), Integer.valueOf(24 * 60 * 60));
        filterData.put(getString(R.string.one_week), Integer.valueOf(7 * 24 * 60 * 60));
        Set<String> keys = filterData.keySet();
        items = keys.toArray(new String[keys.size()]);
    }

    private void showFilterDialog() {
        new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = items[which];
                        int value = filterData.get(key);
                        showData(value * 1000);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.btn_date_from)
    public void showDateFromPickerDialog() {
        filterMode = FilterMode.FROM_FILTER;
        DatePickerFragment.getInstance(yearFrom, monthFrom, dayFrom).show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.btn_date_to)
    public void showDateToPickerDialog() {
        filterMode = FilterMode.TO_FILTER;
        DatePickerFragment.getInstance(yearTo, monthTo, dayTo).show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.btn_time_from)
    public void showTimeFromPickerDialog() {
        filterMode = FilterMode.FROM_FILTER;
        TimePickerFragment.getInstance(hourFrom, minuteFrom).show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.btn_time_to)
    public void showTimeToPickerDialog() {
        filterMode = FilterMode.TO_FILTER;
        TimePickerFragment.getInstance(hourTo, minuteTo).show(getSupportFragmentManager(), null);
    }

    public void setDateFilter(int year, int month, int day) {
        if (filterMode == FilterMode.FROM_FILTER) {
            yearFrom = year;
            monthFrom = month;
            dayFrom = day;
        } else if (filterMode == FilterMode.TO_FILTER) {
            yearTo = year;
            monthTo = month;
            dayTo = day;
        }
        refreshDateDisplayed();
    }

    public void setTimeFilter(int hour, int minute) {
        if (filterMode == FilterMode.FROM_FILTER) {
            hourFrom = hour;
            minuteFrom = minute;
        } else if (filterMode == FilterMode.TO_FILTER) {
            hourTo = hour;
            minuteTo = minute;
        }
        refreshTimeDisplayed();
    }

    private void refreshDateDisplayed() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearFrom);
        calendar.set(Calendar.MONTH, monthFrom);
        calendar.set(Calendar.DAY_OF_MONTH, dayFrom);
        Date date = calendar.getTime();
        btnDateFrom.setText(Utils.formatDate(date.getTime()));

        calendar.set(Calendar.YEAR, yearTo);
        calendar.set(Calendar.MONTH, monthTo);
        calendar.set(Calendar.DAY_OF_MONTH, dayTo);
        date = calendar.getTime();
        btnDateTo.setText(Utils.formatDate(date.getTime()));

        showData();
    }

    private void refreshTimeDisplayed() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourFrom);
        calendar.set(Calendar.MINUTE, minuteFrom);
        Date date = calendar.getTime();
        btnTimeFrom.setText(Utils.formatTimeWithoutSeconds(date.getTime()));

        calendar.set(Calendar.HOUR_OF_DAY, hourTo);
        calendar.set(Calendar.MINUTE, minuteTo);
        date = calendar.getTime();
        btnTimeTo.setText(Utils.formatTimeWithoutSeconds(date.getTime()));

        showData();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static final String EXTRA_HOUR = "EXTRA_HOUR";
        public static final String EXTRA_MINUTE = "EXTRA_MINUTE";

        public static TimePickerFragment getInstance(int hour, int minute) {
            TimePickerFragment fragment = new TimePickerFragment();
            Bundle b = new Bundle();
            b.putInt(EXTRA_HOUR, hour);
            b.putInt(EXTRA_MINUTE, minute);
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = getArguments().getInt(EXTRA_HOUR);
            int minute = getArguments().getInt(EXTRA_MINUTE);
            if (hour == 0 && minute == 0) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
            return dialog;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            MapResultsActivity activity = (MapResultsActivity) getActivity();
            activity.setTimeFilter(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static final String EXTRA_YEAR = "EXTRA_YEAR";
        public static final String EXTRA_MONTH = "EXTRA_MONTH";
        public static final String EXTRA_DAY = "EXTRA_DAY";

        public static DatePickerFragment getInstance(int year, int month, int day) {
            DatePickerFragment fragment = new DatePickerFragment();
            Bundle b = new Bundle();
            b.putInt(EXTRA_YEAR, year);
            b.putInt(EXTRA_MONTH, month);
            b.putInt(EXTRA_DAY, day);
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = getArguments().getInt(EXTRA_YEAR);
            int month = getArguments().getInt(EXTRA_MONTH);
            int day = getArguments().getInt(EXTRA_DAY);
            if (year == 0) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            MapResultsActivity activity = (MapResultsActivity) getActivity();
            activity.setDateFilter(year, month, day);
        }
    }

    enum FilterMode {
        FROM_FILTER, TO_FILTER;
    }

}
