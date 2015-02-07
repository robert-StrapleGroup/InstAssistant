package com.StrapleGroup.instassistant.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.StrapleGroup.instassistant.InstagramInstance;
import com.StrapleGroup.instassistant.R;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.exceptions.InstagramException;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {
    private static final Token TOKEN = null;
    private Token access_token = null;
    private Button signInButton;
    InstagramService service;
    private WebView loginView;
    InstagramInstance instagramInstance;
    Context context;
    String usrname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setOnClickListener(this);
        loginView = (WebView) findViewById(R.id.login_view);
        instagramInstance = InstagramInstance.getInstance(context);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean instagramAuthorize() {
        service = new InstagramAuthService().apiKey(getString(R.string.instagram_client_id))
                .apiSecret(getString(R.string.instagram_secret)).callback("http://www.straplegroup.com")
                .scope("likes comments").build();
        String pAuthUrl = service.getAuthorizationUrl(TOKEN);
        loginView.setVerticalScrollBarEnabled(false);
        loginView.setHorizontalScrollBarEnabled(false);
        loginView.setWebViewClient(new LoginScreen());
        loginView.getSettings().setJavaScriptEnabled(true);
        loginView.loadUrl(pAuthUrl);
        return true;
    }

    private class LoginScreen extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            if (url.startsWith("http://www.straplegroup.com")) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        Verifier pVerifier = new Verifier(url.substring(url.indexOf("=") + 1));
                        access_token = service.getAccessToken(TOKEN, pVerifier);
                        instagramInstance.startService(access_token);
                        try {
                            usrname = instagramInstance.getInstagram().getCurrentUserInfo().getData().getUsername();
                        } catch (InstagramException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute(null, null, null);
                loginView.setEnabled(false);
                loginView.setAlpha(0);
                return true;
            }
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (loginView.getAlpha() == 0) {
            Toast.makeText(getApplicationContext(), usrname, Toast.LENGTH_SHORT).show();
            return;
        } else
            instagramAuthorize();
    }
}
