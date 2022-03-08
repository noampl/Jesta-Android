package com.example.jesta.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.MyApplication;
import com.example.jesta.R;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.google.android.libraries.places.api.Places;

import java.util.Locale;

/**
 * This activity uses as an intro to the app, display intro screen and tries to connect the server
 */
public class introActivity extends AppCompatActivity {

    // region Members

    private final AppCompatActivity activity = this;

    // endregion

    // region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LoginRegisterViewModel viewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
        viewModel.getIsLoggedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                Intent intent = null;
                if (isLoggedIn)
                    intent = new Intent(activity,MainActivity.class);
                else{
                    intent = new Intent(activity,LoginRegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
        // init Google places instance with an hebrew language code
        if (!Places.isInitialized()) {
            Places.initialize(MyApplication.getAppContext(), getString(R.string.google_maps_key), Locale.forLanguageTag("he"));
        }
        viewModel.initLogin(MyApplication.getAppContext());

    }

    // endregion
}