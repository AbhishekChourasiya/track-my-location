package co.techmagic.hi.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class HiParseUtil {

    private static final String TAG = "ParseUtil";

    public static String getChanelNameByFacebookId(String facebookId) {
        return "hi_" + facebookId;
    }

    public static void subscribePushes(String chanel) {
        ParsePush.subscribeInBackground(chanel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e(TAG, "failed to subscribe for push", e);
                }
            }
        });
    }

    public static void unSubscribePushes(String chanel) {
        ParsePush.unsubscribeInBackground(chanel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully unsubscribed to the broadcast channel.");
                } else {
                    Log.e(TAG, "failed to unsubscribe for push", e);
                }
            }
        });
    }

}
