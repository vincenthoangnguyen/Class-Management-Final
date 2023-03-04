package com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.databinding.ActivityClassEt4710Binding;

public class ClassET4710 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityClassEt4710Binding binding;
    TextView weekTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassEt4710Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarClassEt4710Test.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        weekTextView = findViewById(R.id.week_number_tv);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_class_et4710_test);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        // set Text for nav header
        TextView headerEmail = headerView.findViewById(R.id.nav_email);
        TextView headerName = headerView.findViewById(R.id.nav_name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        headerEmail.setText(user.getEmail());
        SharedPreferences sharedPref = getSharedPreferences("mypref", MODE_PRIVATE);
        String username = sharedPref.getString("userName", "");
        String WeekNumber = sharedPref.getString("WeekNumber", "");
        headerName.setText(username);
        weekTextView.setText(WeekNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_e_t4710, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_class_et4710_test);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setUpWeekNumber(MenuItem item) {
       weekTextView.setText(item.getTitle().toString());
        SharedPreferences sharedPref = getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("WeekNumber",item.getTitle().toString());
        editor.apply();
        recreate();
    }
    public void Recreate(){
        recreate();
    }
}