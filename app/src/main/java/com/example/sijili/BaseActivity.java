package com.example.sijili;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sijili.other.NavigationUtil;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigationDrawer() {
        // Find the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Find the toolbar and menu button
        View menuButton = findViewById(R.id.menu_btn);

        if (menuButton != null) {
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNavigationMenu();
                }
            });
        }

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void openNavigationMenu() {
        // Open the navigation menu
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showToast("Home selected");
        } else if (id == R.id.nav_profile) {
            showToast("Profile selected");
        } else if (id == R.id.nav_professional_account) {
            showToast("Pro Account selected");
        } else if (id == R.id.nav_dark_mode) {
            showToast("Dark Mode selected");
        } else if (id == R.id.nav_languages) {
            showToast("Languages selected");
        } else if (id == R.id.nav_rate_us) {
            showToast("Rate Us selected");
        } else if (id == R.id.nav_logout) {
            showToast("Logout selected");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
