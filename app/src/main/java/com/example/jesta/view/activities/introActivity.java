package com.example.jesta.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.jesta.R;
import com.example.jesta.viewmodel.LoginRegisterViewModel;

/**
 * This activity uses as an intro to the app, display intro screen and tries to connect the server
 */
public class introActivity extends AppCompatActivity {

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
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                else{
                    intent = new Intent(getApplicationContext(),LoginRegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
        viewModel.initLogin(getApplicationContext());
    }

    // endregion
}