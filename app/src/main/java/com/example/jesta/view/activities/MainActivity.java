package com.example.jesta.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jesta.R;
import com.example.jesta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // region Members

    private ActivityMainBinding _binding;
    private NavController _navController;
    private AppBarConfiguration _appBarConfiguration;

    // endregion

    // region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        // make sure the window is RTL
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        init();
    }

    // endregion

    // region Private Methods

    private void init(){
        setSupportActionBar(_binding.mainToolbar);
        NavHostFragment navigationHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        _navController = navigationHost.getNavController();

        // init NavBar and pass all the drawer items
        _appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_request_jestas,
                        R.id.nav_todo_jestas, R.id.nav_waiting_jestas, R.id.nav_done_jestas, R.id.nav_jesta_settings,
                        R.id.nav_help,R.id.nav_contact , R.id.nav_about, R.id.nav_privacy, R.id.nav_terms)
                        .setOpenableLayout(_binding.drawerLayout)
                        .build();

        NavigationUI.setupActionBarWithNavController(this, _navController, _appBarConfiguration);
        NavigationUI.setupWithNavController(_binding.navView, _navController);

        // listen to toolbar items click
        _binding.mainToolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    // endregion

    // region Override

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(_navController, _appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // endregion

    // region Listener

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){

                case R.id.nav_notification:
                    _navController.navigate(R.id.nav_notification);
                    return true;
                case R.id.nav_podium:
                    _navController.navigate(R.id.nav_podium);
                    return true;

                case R.id.nav_profile_settings:
                    _navController.navigate(R.id.nav_profile_settings);
                    return true;
                default:
                    System.out.println("peleg - item pressed " + item.getItemId());
                    break;
            }
            return false;
        }
    };

    // endregion
}