package com.example.jesta.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.R;
import com.example.jesta.databinding.ActivityMainBinding;
import com.example.jesta.viewmodel.NotificationViewModel;
import com.example.jesta.workes.NotificationWorker;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // region Members

    private ActivityMainBinding _binding;
    private NavController _navController;
    private AppBarConfiguration _appBarConfiguration;
    private TextView _notificationNumber;
    private CardView _notificationCard;

    // endregion

    // region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        // make sure the window is RTL
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        _notificationNumber = findViewById(R.id.notification_number);
        _notificationCard = findViewById(R.id.notification_container);
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
        initWorkers();
        initObservers();
    }

    private void initWorkers(){
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class,1, TimeUnit.MINUTES)
                .addTag("Notification")
                .build();
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }

    private void initObservers(){
        NotificationViewModel viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.get_notificationTransaction().observe(this, new Observer<List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction>>() {
            @Override
            public void onChanged(List<GetAllUserFavorsRequestedTransactionQuery.GetAllUserFavorsRequestedTransaction> transactions) {
                if (transactions != null) {
                    System.out.println("peleg - map size is " + transactions.size());
                    if (transactions.size() > 0) {
                        _notificationCard.setVisibility(View.VISIBLE);
                    } else {
                        _notificationCard.setVisibility(View.INVISIBLE);
                    }
                    _notificationNumber.setText(String.valueOf(transactions.size()));
                }
            }
        });
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
        // THis is a patch for getting the action view clickes
        final Menu m = menu;
        final MenuItem item = menu.findItem(R.id.nav_notification);
        item.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m.performIdentifierAction(item.getItemId(), 0);
            }
        });
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