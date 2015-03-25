package co.techmagic.hi;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.List;

import co.techmagic.hi.data.DataHelper;
import co.techmagic.hi.data.model.HiFriendRecord;
import co.techmagic.hi.webclient.model.FriendsPushResponse;

public class HiPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private static final String EXTRA_PARSE_DATA = "com.parse.Data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra(EXTRA_PARSE_DATA);
        String message = "No friends near you :(";
        if (data != null) {
            try {
                Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
                FriendsPushResponse response = gson.fromJson(data, FriendsPushResponse.class);

                List<HiFriendRecord> friends = response.getHiFriendRecords();
                if (friends != null && friends.size() > 0) {
                    DataHelper dataHelper = DataHelper.getInstance(context);
                    dataHelper.saveHiFriendRecords(friends, System.currentTimeMillis());
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("Hi from ");
                    for (HiFriendRecord f : friends) {
                        messageBuilder.append(" ");
                        messageBuilder.append(f.getName());
                        messageBuilder.append(",");
                    }
                    messageBuilder.deleteCharAt(messageBuilder.length() - 1);
                    message = messageBuilder.toString();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("alert", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra(EXTRA_PARSE_DATA, jsonObject.toString());
        super.onReceive(context, intent);
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
