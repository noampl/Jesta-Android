package com.example.jesta.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.jesta.R;
import com.example.jesta.databinding.ActivityLoginRegisterBinding;

public class LoginRegisterActivity extends AppCompatActivity {

    // region Members

    private ActivityLoginRegisterBinding _binding;

    // endregion

    // region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_login_register);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    // endregion

}