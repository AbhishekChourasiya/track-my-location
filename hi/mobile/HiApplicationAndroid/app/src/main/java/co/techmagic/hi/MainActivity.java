package co.techmagic.hi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFacebookLogin();
    }

    private void checkFacebookLogin() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            showLoginActivity();
        }
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        Session session = ParseFacebookUtils.getSession();
        if (session != null && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        ParseUser.logOut();
        showLoginActivity();
    }

    private void showLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
