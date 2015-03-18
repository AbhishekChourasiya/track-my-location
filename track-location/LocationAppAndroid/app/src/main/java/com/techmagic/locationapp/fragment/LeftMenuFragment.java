package com.techmagic.locationapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techmagic.locationapp.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.techmagic.hi.R;

public class LeftMenuFragment extends Fragment {

    @InjectView(R.id.rv_items) RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, null);
        ButterKnife.inject(this, v);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        MenuAdapter adapter = new MenuAdapter((BaseActivity) getActivity());
        recyclerView.setAdapter(adapter);

        return v;
    }

}
