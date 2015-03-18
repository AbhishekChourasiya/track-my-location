package com.techmagic.locationapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techmagic.locationapp.activity.BaseActivity;
import com.techmagic.locationapp.activity.TrackGeoFenceActivity;
import com.techmagic.locationapp.activity.TrackLocationActivity;

import java.util.ArrayList;
import java.util.List;

import co.techmagic.hi.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private BaseActivity activity;
    private List<MenuModel> items;

    public MenuAdapter(BaseActivity activity) {
        super();
        this.activity = activity;
        items = new ArrayList<MenuModel>();
        items.add(new MenuModel("Track my location", TrackLocationActivity.class));
        items.add(new MenuModel("Track geo fences", TrackGeoFenceActivity.class));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MenuModel model = items.get(position);

        holder.tvItem.setText(model.title);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.activity.equals(activity.getClass())) {
                    Intent i = new Intent(activity, model.activity);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(i);
                }
                activity.closeDrawer();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MenuModel {
        String title;
        Class activity;
        public MenuModel(String title, Class activity) {
            this.title = title;
            this.activity = activity;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tvItem;
        ImageView ivIcon;
        public ViewHolder(View v) {
            super(v);
            view = v;
            tvItem = (TextView) v.findViewById(R.id.tv_item);
        }
    }
}
