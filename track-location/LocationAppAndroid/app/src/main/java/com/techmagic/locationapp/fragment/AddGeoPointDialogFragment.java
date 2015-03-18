package com.techmagic.locationapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.techmagic.locationapp.activity.TrackGeoFenceActivity;
import com.techmagic.locationapp.data.model.GeoPoint;

import butterknife.ButterKnife;
import co.techmagic.hi.R;

public class AddGeoPointDialogFragment extends DialogFragment {

    private static final String KEY_LAT_LNG = "KEY_LAT_LNG";
    private AlertDialog dialog;

    public static AddGeoPointDialogFragment getInstance(LatLng latLng) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_LAT_LNG, latLng);
        AddGeoPointDialogFragment fragment = new AddGeoPointDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_geo_point_dialog, null);
        final EditText etName = ButterKnife.findById(view, R.id.et_geo_name);
        final TextView tvMeters = ButterKnife.findById(view, R.id.tv_meters);
        Button btnAdd = ButterKnife.findById(view, R.id.btn_add);
        Button btnCancel = ButterKnife.findById(view, R.id.btn_cancel);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (charSequence.length() == 0) {
                    btnAdd.setEnabled(false);
                } else {
                    btnAdd.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final SeekBar seekBar = ButterKnife.findById(view, R.id.seek_bar);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMeters.setText(progress + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(100);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Geo Fence");
        builder.setView(view);
        dialog = builder.create();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackGeoFenceActivity activity = (TrackGeoFenceActivity) getActivity();
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    etName.setError("Name cannot be empty");
                } else if (activity.nameExists(name)) {
                    etName.setError("This name already exists");
                } else {
                    int radius = seekBar.getProgress();
                    LatLng latLng = getArguments().getParcelable(KEY_LAT_LNG);
                    GeoPoint geoPoint = GeoPoint.getInstance(name, latLng.latitude, latLng.longitude, radius);
                    activity.addGeoPoint(geoPoint);
                    dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

}
