package org.book2words;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import org.book2words.screens.LoginFragment;
import org.book2words.services.B2WHandler;
import org.book2words.services.B2WService;


public class SplashActivity extends Activity {

    private static final String LOGIN_FRAGMENT_TAG = "fragment:login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B2WService.Companion.checkLogin(this, new B2WHandler<Boolean>() {
            @Override
            public void onResult(boolean success, Boolean data) {
                if (success) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    Fragment fragment = getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
                    if (fragment == null) {
                        fragment = new LoginFragment();

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.add(android.R.id.content, fragment, LOGIN_FRAGMENT_TAG);
                        transaction.commit();
                    }
                }
            }
        });
    }

}
