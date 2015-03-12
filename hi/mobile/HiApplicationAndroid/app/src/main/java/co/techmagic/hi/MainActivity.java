package co.techmagic.hi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.techmagic.hi.model.User;
import co.techmagic.hi.util.HiParseUtil;


public class MainActivity extends ActionBarActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    private void checkLogin() {
        if ((user = HiPreferencesManager.getUser(getApplicationContext())) == null) {
            showLoginActivity();
        }
    }

    private void checkFacebookLogin() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            showLoginActivity();
        }
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        HiParseUtil.unSubscribePushes(HiParseUtil.getChanelNameByFacebookId(user.getFacebookId()));
        HiPreferencesManager.deleteUser(getApplicationContext());
        logoutFacebook();
        showLoginActivity();
    }

    private void logoutFacebook() {
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        Session session = ParseFacebookUtils.getSession();
        if (session != null && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        ParseUser.logOut();
    }

    private void showLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
