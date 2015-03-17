package co.techmagic.hi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.techmagic.hi.webclient.model.User;
import co.techmagic.hi.util.HiParseUtil;
import co.techmagic.hi.webclient.HiClient;
import co.techmagic.hi.webclient.IHiClient;
import co.techmagic.hi.webclient.model.SignInResponse;


public class LoginActivity extends ActionBarActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }


    @OnClick(R.id.btn_login)
    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();

                if (err != null) {
                    Toast.makeText(LoginActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (user == null) {
                    showMessage("The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    showMessage("User signed up and logged in through Facebook!");
                    onSuccess();
                } else {
                    showMessage("User logged in through Facebook!");
                    onSuccess();
                }
            }
        });
    }

    private void onSuccess() {
        getFaceBookGraphObject();
    }

    private void showMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getFaceBookGraphObject() {
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        Session session =  ParseFacebookUtils.getSession();
        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            @Override
            public void onCompleted(GraphUser graphUser, Response response) {
                if (graphUser != null) {
                    String facebookId = graphUser.getId();
                    String firstName = graphUser.getFirstName();
                    String lastName = graphUser.getLastName();
                    String name = firstName + " " + lastName;
                    String genderString = graphUser.asMap().get("gender").toString();
                    String imageUrl = "http://graph.facebook.com/" + facebookId + "/picture?type=large";
                    User user = new User(facebookId, name, imageUrl, genderString);
                    login(user);
                }
            }

        });
    }

    private void showMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void login(final User user) {
        new AsyncTask<Void, Void, SignInResponse>() {
            @Override
            protected SignInResponse doInBackground(Void... params) {
                IHiClient client = new HiClient();
                SignInResponse response = null;
                try {
                    response = client.signIn(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(SignInResponse response) {
                super.onPostExecute(response);
                if (response != null && response.getStatus() == IHiClient.RESPONSE_CODE_OK) {
                    HiPreferencesManager.saveUser(user, getApplicationContext());
                    HiParseUtil.subscribePushes(HiParseUtil.getChanelNameByFacebookId(user.getFacebookId()));
                    Toast.makeText(LoginActivity.this, user.getName() + " is logged in",Toast.LENGTH_SHORT).show();
                    showMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, user.getName() + " is not logged in",Toast.LENGTH_SHORT).show();
                }

            }
        }.execute();
    }

}
