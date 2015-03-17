package com.techmagic.locationapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_geo_point, null);
        EditText etName = ButterKnife.findById(view, R.id.et_geo_name);
        final TextView tvMeters = ButterKnife.findById(view, R.id.tv_meters);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Button btnSubmit = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (charSequence.length() == 0) {
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        SeekBar seekBar = ButterKnife.findById(view, R.id.seek_bar);
        seekBar.setMax(5000);
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
        seekBar.setProgress(1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Geo Fence");
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        return dialog;
    }

}
