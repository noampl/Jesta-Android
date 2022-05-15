package com.example.jesta.view.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesta.R;
import com.example.jesta.common.AlertDialogRtlHelper;
import com.example.jesta.common.IntentUtils;
import com.example.jesta.databinding.ActivityMainBinding;
import com.example.jesta.databinding.BellBinding;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.viewmodel.NotificationViewModel;
import com.example.jesta.workes.FavorsWorker;
import com.example.jesta.workes.NotificationWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener, NavigationView.OnNavigationItemSelectedListener {

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
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//         make sure the window is RTL
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        _notificationNumber = findViewById(R.id.notification_number);
        _notificationCard = findViewById(R.id.notification_container);
        init();
    }

    @Override
    public void onBackPressed() {
        // If current fragment is the main screen (the map) then confirms exit:
        if (_navController.getCurrentDestination().getId() == R.id.nav_map) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.are_you_sure_to_exist);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });
            builder.setNegativeButton(R.string.no, null);
            AlertDialogRtlHelper.make(builder).show();
        } else {
            super.onBackPressed();
        }
    }

    // endregion

    // region Private Methods

    private void init() {
        setSupportActionBar(_binding.mainToolbar);
        NavHostFragment navigationHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);
        _navController = navigationHost.getNavController();

        // init NavBar and pass all the drawer items
        _appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_requested_jestas,
                        R.id.nav_todo_jestas, R.id.nav_waiting_jestas, R.id.nav_done_jestas, R.id.nav_jesta_settings,
                        R.id.nav_help, R.id.nav_contact, R.id.nav_about, R.id.nav_privacy, R.id.nav_terms)
                        .setOpenableLayout(_binding.drawerLayout)
                        .build();

        _navController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, _navController, _appBarConfiguration);
        NavigationUI.setupWithNavController(_binding.navView, _navController);
        _binding.navView.setNavigationItemSelectedListener(this);

        // listen to toolbar items click
        _binding.mainToolbar.setOnMenuItemClickListener(menuItemClickListener);
        initWorkers();
        initObservers();
        initServices();
//        initListeners();
    }

    private void initWorkers() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.MINUTES)
                .addTag("Notification")
                .build();
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);


        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(FavorsWorker.class, 30, TimeUnit.SECONDS)
                .addTag("Get_Favors")
                .setInitialDelay(1,TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueue(periodicWork);
    }

//    private void initListeners(){
//        _binding.belllll.bellContainer.setOnClickListener(v->_navController.navigate(R.id.nav_notification));
//    }

    private void initObservers() {
        NotificationViewModel viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.get_notificationTransaction().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                System.out.println("peleg - transacions null ? " + (transactions == null) + " thread is " + Thread.currentThread());
                System.out.println("peleg - start is card visible " + _notificationCard.getVisibility());
                if (transactions != null) {
                    if (transactions.size() > 0) {
                        _notificationCard.setVisibility(View.VISIBLE);
                        System.out.println("peleg - set card visible");
                    } else {
                        _notificationCard.setVisibility(View.INVISIBLE);
                        System.out.println("peleg - set card invisible");
                    }
                    _notificationNumber.setText(String.valueOf(transactions.size()));
                    System.out.println("peleg - transactionsize " + transactions.size());
                    System.out.println("peleg - end is card visible " + _notificationCard.getVisibility());
                    _binding.mainToolbar.getMenu().findItem(R.id.nav_notification);
                }
            }
        });
    }

    private void initServices() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        GraphqlRepository.getInstance().addUserToken(token);
                    }
                });
    }

    private void updateToolbarByNavScreen(int id) {
        if (id == R.id.nav_map || id == R.id.nav_podium) {
            _binding.frameToolbarLogo.setVisibility(View.VISIBLE);
        } else {
            _binding.frameToolbarLogo.setVisibility(View.GONE);
        }
    }

    private void contactUs() {
        Intent mailIntent = IntentUtils.gmail(this, getString(R.string.jesta_mail));
        startActivity(mailIntent);
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

    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        this.updateToolbarByNavScreen(navDestination.getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // In order to be able to listen on item selections, we need to override the Navigation Drawer item selection behavior:
        int id = item.getItemId();
        if (id == R.id.nav_contact) {
            this.contactUs();
        }
        // This is for maintaining the behavior of the Navigation Drawer:
        NavigationUI.onNavDestinationSelected(item, _navController);
        if (_binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    // endregion

    // region Listener

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_notification:
                    _navController.navigate(R.id.nav_notification);
                    return true;
                case R.id.nav_podium:
                    _navController.navigate(R.id.nav_podium);
                    return true;
                case R.id.nav_user_profile:
                    _navController.navigate(R.id.nav_user_profile);
                    return true;
                case R.id.nav_user_settings:
                    _navController.navigate(R.id.nav_profile_settings);
                    return true;
                default:
                    Log.e("onMenuItemClick", "unrecognized item pressed " + item.getItemId());
                    break;
            }
            return false;
        }
    };

    // endregion
}