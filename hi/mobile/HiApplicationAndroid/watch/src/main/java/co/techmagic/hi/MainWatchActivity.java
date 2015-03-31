package co.techmagic.hi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainWatchActivity extends Activity {
    public static final String EXTRA_DATA = "EXTRA_DATA";
    @InjectView(R.id.rv_friends)
    ListView rvFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        ButterKnife.inject(this);
        retrieveData();
    }

    private void retrieveData() {
        Intent i = getIntent();
        if (i.hasExtra(EXTRA_DATA)) {
            byte[] data = i.getByteArrayExtra(EXTRA_DATA);
            parseData(data);
        }
    }

    private void parseData(byte[] data) {
        if (data != null) {
            Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
            String dataString = new String(data);
            Log.d("parseData", "data string is " + dataString);
            FriendsPushResponse response = gson.fromJson(dataString, FriendsPushResponse.class);
            List<HiFriendRecord> friends = response.getHiFriendRecords();
            HiFriendRecord record = new HiFriendRecord();
            record.setName("name");
            record.setFacebookId("fb id");
            record.setImageUrl("https://pbs.twimg.com/profile_images/522909800191901697/FHCGSQg0.png");
            record.setGender("male");
            friends.add(record);
            setupListView(friends);
        }
    }

    private void setupListView(List<HiFriendRecord> friends) {
        HiFriendsListAdapter hiFriendsAdapter = new HiFriendsListAdapter(getApplicationContext());
        rvFriends.setAdapter(hiFriendsAdapter);
        hiFriendsAdapter.refresh(friends);
    }

}
