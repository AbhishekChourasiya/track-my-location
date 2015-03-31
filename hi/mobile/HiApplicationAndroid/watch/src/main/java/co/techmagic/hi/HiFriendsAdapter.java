package co.techmagic.hi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import co.techmagic.hi.Utils;

public class HiFriendsAdapter extends RecyclerView.Adapter<HiFriendsAdapter.ViewHolder> {

    private Context context;
    private List<HiFriendRecord> friends;

    public HiFriendsAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HiFriendRecord friend = friends.get(position);

        holder.tvName.setText(friend.getName());
        ImageLoader.getInstance().displayImage(friend.getImageUrl(), holder.ivIcon);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You've chosen " + friend.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void refresh(List<HiFriendRecord> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tvName;
        TextView tvTime;
        ImageView ivIcon;
        public ViewHolder(View v) {
            super(v);
            view = v;
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        }
    }
}
