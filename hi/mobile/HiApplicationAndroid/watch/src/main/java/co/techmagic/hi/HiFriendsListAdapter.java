package co.techmagic.hi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class HiFriendsListAdapter extends BaseAdapter {
    private Context context;
    private List<HiFriendRecord> friends;
    private LayoutInflater inflater;

    public HiFriendsListAdapter(Context context) {
        super();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (friends == null) {
            return 0;
        }
        return friends.size();
    }

    @Override
    public HiFriendRecord getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item_friend, null);

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);

        HiFriendRecord friend = getItem(position);

        tvName.setText(friend.getName());
        ImageLoader.getInstance().displayImage(friend.getImageUrl(), ivIcon);

        return convertView;
    }

    public void refresh(List<HiFriendRecord> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }
}
